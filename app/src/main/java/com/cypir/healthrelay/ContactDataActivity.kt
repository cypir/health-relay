package com.cypir.healthrelay

import android.arch.lifecycle.ViewModelProviders
import android.content.ContentResolver
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


class ContactDataActivity : AppCompatActivity() {

    lateinit var phoneDataAdapter : ContactDataAdapter
    lateinit var emailDataAdapter : ContactDataAdapter

    lateinit var vm : ContactDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_data)

        vm = ViewModelProviders.of(this).get(ContactDataViewModel::class.java)

        val extras = intent.extras
        val contactUri = extras.getParcelable<Uri>("contactUri")

        //initialize empty adapters
        phoneDataAdapter = ContactDataAdapter(this@ContactDataActivity, arrayListOf())
        rv_phone_numbers.adapter = phoneDataAdapter
        rv_phone_numbers.layoutManager = LinearLayoutManager(this@ContactDataActivity)

        emailDataAdapter = ContactDataAdapter(this@ContactDataActivity, arrayListOf())
        rv_email_addresses.adapter = emailDataAdapter
        rv_email_addresses.layoutManager = LinearLayoutManager(this@ContactDataActivity)

        launch(UI) {

            //gets contact cursor
            val cursor = bg {
                this@ContactDataActivity.contentResolver?.query(contactUri, null, null, null, null)
            }.await()

            if(cursor != null) {
                cursor.moveToFirst()

                vm.contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                text_name.text = vm.contactName

                vm.contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))


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
        inflater.inflate(R.menu.menu_add_contact, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.add_contact_save -> {
            // User chose the "Settings" item, show the app settings UI...
            Toast.makeText(this, "add_contact_save", Toast.LENGTH_LONG).show()

            //get the in memory selections from the adapters
            val phones = phoneDataAdapter.HRContactData
            val emails = emailDataAdapter.HRContactData


            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME_PRIMARY


//            async(UI){
//                bg { vm.saveContact(vm.contactId, combined) }
//            }


            //get selected
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
        val cachedHRContactData = HashMap<String, List<HRContactData>>()

        return async(UI) {

            if(c != null){
                //iterate through each Data entry for each top level aggregated contract
                while (c.moveToNext()) {
                    val data = c.getString(c.getColumnIndex(Data.DATA1))
                    val dataId = c.getString(c.getColumnIndex(Data._ID))
                    val mimetype = c.getString(c.getColumnIndex(Data.MIMETYPE))
                    val rawContactId = c.getString(c.getColumnIndex(Data.RAW_CONTACT_ID))

                    //if we haven't cached this raw_contact's info yet, then get HR info from db and cache
                    //we cache because a single raw contact may have multiple Data entries (phone, email).
                    if(cachedHRContactData[rawContactId] == null){

                        //get additional data that we stored about this particular raw contact
                        val hrContactData = bg { vm.getHRContactData(rawContactId) }.await()

                        //if we do have some results, cache it
                        if(hrContactData != null){
                            cachedHRContactData[rawContactId] = hrContactData
                        }else{
                            //if we don't have any stored information for this raw contact, then we set it to an empty list
                            cachedHRContactData[rawContactId] = listOf()
                        }
                    }

                    //filter through the cached list. If we find a matching Data._ID, use it to populate the isEnabled field.
                    //We have @Ignore fields on HRContactData so the UI can show the actual data field
                    val hrContactData = cachedHRContactData[rawContactId]?.find { it.id == dataId }

                    if(hrContactData != null){
                        dataList.add(HRContactData(id=dataId, mimetype=mimetype, data=data, rawContactId = rawContactId, isEnabled=hrContactData.isEnabled))
                    }else{
                        dataList.add(HRContactData(id=dataId, mimetype=mimetype, data=data, rawContactId = rawContactId))
                    }
                }

                c.close()
            }else{
                Log.e("HealthRelay","cursor was null")
            }

            return@async dataList
//            dataAdapter.initialize()
//            dataAdapter.notifyDataSetChanged()
        }


    }

}

