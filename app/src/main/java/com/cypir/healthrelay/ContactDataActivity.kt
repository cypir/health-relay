package com.cypir.healthrelay

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.Data
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.cypir.healthrelay.adapter.ContactDataAdapter
import com.cypir.healthrelay.viewmodel.ContactDataViewModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import com.cypir.healthrelay.entity.HRContactData
import kotlinx.android.synthetic.main.activity_contact_data.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch
import android.content.ContentProviderOperation
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class ContactDataActivity : AppCompatActivity(), ContactDataAdapter.OnDataEnabled {
    lateinit var phoneDataAdapter : ContactDataAdapter
    lateinit var emailDataAdapter : ContactDataAdapter

    var hasDataPerms = false

    lateinit var vm : ContactDataViewModel

    override fun dataPermEnabled(mimetype: String): Boolean {
        //check mimetype
        when(mimetype){
            Phone.CONTENT_ITEM_TYPE -> {
                getDataPerms()
                return hasDataPerms
            }
        }
        return true
    }

    @AfterPermissionGranted(2)
    fun getDataPerms(){
        val perms = arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE)

        val hasPermissions = EasyPermissions.hasPermissions(this,
                *perms)

        if (hasPermissions){
            hasDataPerms = true
        }else{
            EasyPermissions.requestPermissions(this,
                    "We need to be able to send SMS and read phone state in order to notify this contact.",
                    2,
                    *perms
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_data)

        vm = ViewModelProviders.of(this).get(ContactDataViewModel::class.java)

        val extras = intent.extras
        vm.contactUri = extras.getParcelable<Uri>("contactUri")
        vm.hrMimeId = extras.getString("hrMimeId")

        //initialize empty adapters
        phoneDataAdapter = ContactDataAdapter(this@ContactDataActivity, arrayListOf(), this)
        rv_phone_numbers.adapter = phoneDataAdapter
        rv_phone_numbers.layoutManager = LinearLayoutManager(this@ContactDataActivity)

        emailDataAdapter = ContactDataAdapter(this@ContactDataActivity, arrayListOf(), this)
        rv_email_addresses.adapter = emailDataAdapter
        rv_email_addresses.layoutManager = LinearLayoutManager(this@ContactDataActivity)

        launch(UI) {

            //gets contact cursor
            val cursor = bg {
                this@ContactDataActivity.contentResolver?.query(vm.contactUri, null, null, null, null)
            }.await()

            if(cursor != null) {
                cursor.moveToFirst()

                vm.contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                text_name.text = vm.contactName

                vm.contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID))


                val dataList = getDataList().await()
                Log.d("HealthRelay",dataList.toString())

                emailDataAdapter.HRContactData = dataList.filter { it.mimetype == Email.CONTENT_ITEM_TYPE }
                emailDataAdapter.notifyDataSetChanged()

                phoneDataAdapter.HRContactData = dataList.filter { it.mimetype == Phone.CONTENT_ITEM_TYPE }
                phoneDataAdapter.notifyDataSetChanged()

                cursor.close()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_contact_data, menu)
        return true
    }

    private fun saveContact(){
        val phones = phoneDataAdapter.HRContactData
        val emails = emailDataAdapter.HRContactData

        val combined = phones + emails

        Log.d("HealthRelay",phones.toString())
        Log.d("HealthRelay",emails.toString())

        //iterate through the user selections in the phones and emails lists. Get the raw_contact_ids.
        //we will need to verify one by one whether or not each of these raw_contact_ids has a
        //health relay mimetype row.

        val existingMimeRows = HashMap<Long, Boolean>()

        //first, figure out which raw contacts already have the HR mimetype associated
        val existingMimeC = contentResolver.query(
                Data.CONTENT_URI,
                arrayOf(
                        Data._ID,
                        Data.RAW_CONTACT_ID
                ),
                Data.MIMETYPE + "= ?",
                arrayOf(vm.MIMETYPE_HRNOTIFY),
                null)

        //store the RAW_CONTACT_ID in the map so we know if a user already has an HR mimetype row
        while(existingMimeC.moveToNext()){
            existingMimeRows[existingMimeC.getLong(existingMimeC.getColumnIndex(Data.RAW_CONTACT_ID))] = true
        }

        existingMimeC.close()

        //now iterate through the list of user selected contacts, adding to the list of in memory mime
        //rows whenever we don't see a match. If we don't see a match, make sure to add to the batch transaction.
        val ops = ArrayList<ContentProviderOperation>()

        combined.forEach {
            //if we don't currently have this mime row or don't intend to add it yet, then we must add it
            if(existingMimeRows[it.rawContactId] == null){
                //add the mime type to the batch transaction
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValue(Data.RAW_CONTACT_ID, it.rawContactId)
                        .withValue(Data.MIMETYPE, vm.MIMETYPE_HRNOTIFY)
                        .withValue(Data.DATA1, true)
                        .build())

                //add to the existingMimeRows (since we intend to add it, we don't want to do a duplicate insert)
                existingMimeRows[it.rawContactId] = true
            }
        }

        //add mimetypes to the db
        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)

        //add hrcontactdata to the db
        launch(UI){
            //now add the combined list to the db
            try{
                bg { vm.saveHRContactData(combined) }.await()
                Toast.makeText(this@ContactDataActivity,
                        this@ContactDataActivity.resources.getString(R.string.contact_data_saved),
                        Toast.LENGTH_LONG).show()
                //after we save, we should close this activity.
                this@ContactDataActivity.finish()
            }catch(e : Exception){
                Log.e("HealthRelay",e.toString())
            }

        }
    }

    private fun removeContact(){

        launch(UI){
            //get all raw_contact_ids associated with this contact that have this mimetype
            val rawContactIdList = arrayListOf<Long>()

            val existingMimeC = bg { contentResolver.query(
                    Data.CONTENT_URI,
                    arrayOf(
                            Data.DISPLAY_NAME, //TODO: just for debug, comment out
                            Data.DATA1, //TODO: just for debug, comment out
                            Data.RAW_CONTACT_ID
                    ),
                    Data.MIMETYPE + "= ? and " + Data.CONTACT_ID + "=?",
                    arrayOf(vm.MIMETYPE_HRNOTIFY, vm.contactId.toString()),
                    null) }.await()

            //we can pass the raw contacts list directly in, as duplicates wont affect in clause
            while(existingMimeC.moveToNext()){
                rawContactIdList.add(existingMimeC.getLong(existingMimeC.getColumnIndex(Data.RAW_CONTACT_ID)))
                //Log.d("HealthRelay",existingMimeC.getString(0) + ": " + existingMimeC.getString(1))
            }

            existingMimeC.close()

            //remove all HR mime type rows for each raw contact associated with this Contact
            val ops = ArrayList<ContentProviderOperation>()

            ops.add(ContentProviderOperation.newDelete(Data.CONTENT_URI)
                    .withSelection(
                            Data.CONTACT_ID + "=? and " + Data.MIMETYPE + "=?",
                            arrayOf(vm.contactId.toString(), vm.MIMETYPE_HRNOTIFY)
                    )
                    .build())
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)

            //then remove db entries from our app db
            bg {vm.removeHrContactData(rawContactIdList)}
        }



    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        //when a user saves their notification selection
        R.id.contact_save -> {
            //get the in memory selections from the adapters
            saveContact()
            true
        }
        R.id.contact_remove -> {
            removeContact()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun getDataList() : Deferred<List<HRContactData>> {

        //get existing list of contact methods
        val cr = this@ContactDataActivity.contentResolver

        val uri = Data.CONTENT_URI

        val c = cr.query(uri, arrayOf
        (
                Data.DATA1,
                Data._ID,
                Data.MIMETYPE,
                Data.RAW_CONTACT_ID
        ),
        Data.CONTACT_ID + " = " + vm.contactId,
        null, null)

        //list of phone numbers, list of emails, etc go in here
        val dataList = arrayListOf<HRContactData>()
        val cachedHRContactData = HashMap<Long, List<HRContactData>>()
        val dataSet = HashSet<String>()

        return async(UI) {

            if(c != null){
                //iterate through each Data entry for each top level aggregated contract
                while (c.moveToNext()) {
                    val data = c.getString(c.getColumnIndex(Data.DATA1)) //TODO check if data is string
                    val dataId = c.getLong(c.getColumnIndex(Data._ID))
                    val mimetype = c.getString(c.getColumnIndex(Data.MIMETYPE))
                    val rawContactId = c.getLong(c.getColumnIndex(Data.RAW_CONTACT_ID))

                    //skip if data is null
                    if(data == null){
                        continue
                    }

                    //if we haven't cached this raw_contact's info yet, then get HR info from db and cache
                    //we cache because a single raw contact may have multiple Data entries (phone, email).
                    //this reduces the number of times we pull from our HR db since we are iterating through
                    //data rows
                    if(cachedHRContactData[rawContactId] == null){

                        //get additional data that we stored about this particular raw contact
                        val hrContactData = bg { vm.getHRContactDataByRawContactId(rawContactId) }.await()

                        //if we do have some results, cache it
                        if(hrContactData != null){
                            cachedHRContactData[rawContactId] = hrContactData
                        }else{
                            //if we don't have any stored information for this raw contact, then we set it to an empty list
                            cachedHRContactData[rawContactId] = listOf()
                        }
                    }

                    /**
                     * Issue #6. If an app is installed that uses an existing contact data (eg, WhatsApp and phone numbers),
                     * that phone number will be displayed multiple times on the selection list. To prevent duplicates,
                     * we store the data in a hashset, which we then check with before adding it to the list of selectable
                     * contact information. This will effectively remove duplicates.
                     */
                    if(!dataSet.contains(data)){
                        //filter through the cached list. If we find a matching Data._ID, use it to populate the isEnabled field.
                        //We have @Ignore fields on HRContactData so the UI can show the actual data field
                        //the presence of hrContactData is enough to make it enabled.
                        val hrContactData = cachedHRContactData[rawContactId]?.find { it.id == dataId }

                        if(hrContactData != null){
                            dataList.add(HRContactData(id=dataId, mimetype=mimetype, data=data, rawContactId = rawContactId, isEnabled=hrContactData.isEnabled))
                        }else{
                            dataList.add(HRContactData(id=dataId, mimetype=mimetype, data=data, rawContactId = rawContactId))
                        }

                        //add this data to the dataSet
                        dataSet.add(data)
                    }
                }

                c.close()
            }else{
                Log.e("HealthRelay","cursor was null")
            }

            return@async dataList
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}

