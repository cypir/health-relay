package com.cypir.healthrelay

import android.arch.lifecycle.ViewModelProviders
import android.content.ContentResolver
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Email
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.cypir.healthrelay.adapter.ContactDataAdapter
import com.cypir.healthrelay.viewmodel.ContactDataViewModel
import kotlinx.android.synthetic.main.activity_contact_data.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import com.cypir.healthrelay.entity.ContactData
import kotlinx.android.synthetic.main.activity_contact_data.text_name


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

        //initialize adapters
        phoneDataAdapter = ContactDataAdapter(this@ContactDataActivity, arrayListOf())
        rv_phone_numbers.adapter = phoneDataAdapter
        rv_phone_numbers.layoutManager = LinearLayoutManager(this@ContactDataActivity)

        emailDataAdapter = ContactDataAdapter(this@ContactDataActivity, arrayListOf())
        rv_email_addresses.adapter = emailDataAdapter
        rv_email_addresses.layoutManager = LinearLayoutManager(this@ContactDataActivity)

        text_name.text = ""

        async(UI) {

            val cursor = bg {
                this@ContactDataActivity.contentResolver?.query(contactUri, null, null, null, null)
            }.await()

            if(cursor != null){
                cursor.moveToFirst()

                vm.contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                text_name.text = vm.contactName

                vm.contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

                //get existing list of contact methods
                val cr = this@ContactDataActivity.contentResolver


                setAdapterList(cr = cr, contactId = vm.contactId,
                        contactInfoType = "phone", dataAdapter = phoneDataAdapter)

                setAdapterList(cr = cr, contactId = vm.contactId,
                        contactInfoType = "email", dataAdapter = emailDataAdapter)


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

            //save contacts
            val phones = phoneDataAdapter.contactData
            val emails = emailDataAdapter.contactData

            val combined = ArrayList<ContactData>()
            combined.addAll(phones)
            combined.addAll(emails)

            async(UI){
                bg { vm.saveContact(combined) }
            }


            //get selected
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun setAdapterList(cr: ContentResolver, contactId : String, contactInfoType: String, dataAdapter : ContactDataAdapter) {

        val uri : Uri
        val columnIndex : String
        val contactInfoIdCol : String
        val contactIdCol: String

        when(contactInfoType){
            "phone" -> {
                uri = Phone.CONTENT_URI
                columnIndex = Phone.NUMBER
                contactInfoIdCol = Phone._ID
                contactIdCol = Phone.CONTACT_ID
            }
            else -> { //if not phone, for now, it must be email
                uri = Email.CONTENT_URI
                columnIndex = Email.ADDRESS
                contactInfoIdCol = Email._ID
                contactIdCol = Email.CONTACT_ID
            }
        }


        val info = cr.query(uri, null,
                "$contactIdCol = $contactId", null, null)

        //list of phone numbers, list of emails, etc go in here
        val infoList = arrayListOf<ContactData>()

        //hide the contact information if it was already used
        async(UI) {

            //get existing contacts
            val contactWithContactData = bg { vm.getStoredContactInfo(contactId) }.await()

            Log.d("HealthRelay", contactWithContactData.contactData.toString())

            if(info != null){
                while (info.moveToNext()) {
                    //TODO: possibly check to see if _ID of col info has changed for duplicate info?
                    val contactInfo = info.getString(info.getColumnIndex(columnIndex))
                    val id = info.getString(info.getColumnIndex(contactInfoIdCol))

                    //set isEnabled to true if our persisted data shows as true
                    val stored = contactWithContactData.contactData.find {
                        it.id == id
                    }

                    if(stored != null && stored.isEnabled){
                        infoList.add(ContactData(id=id, type=contactInfoType, info=contactInfo, contactId = contactId, isEnabled=true))
                    }else{
                        infoList.add(ContactData(id=id, type=contactInfoType, info=contactInfo, contactId = contactId))
                    }
                }

                info.close()
            }

            dataAdapter.contactData = infoList
            dataAdapter.initialize()
            dataAdapter.notifyDataSetChanged()
        }


    }

}

