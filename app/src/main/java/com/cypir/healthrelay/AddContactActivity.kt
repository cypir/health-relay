package com.cypir.healthrelay

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Email
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.cypir.healthrelay.adapter.AddContactAdapter
import com.cypir.healthrelay.adapter.ContactAdapter
import kotlinx.android.synthetic.main.activity_add_contact.*
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class AddContactActivity : AppCompatActivity() {

    lateinit var phoneAdapter : AddContactAdapter
    lateinit var emailAdapter : AddContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val extras = intent.extras
        val contactUri = extras.getParcelable<Uri>("contactUri")

        //initialize adapters
        phoneAdapter = AddContactAdapter(this@AddContactActivity, arrayListOf())
        rv_phone_numbers.adapter = phoneAdapter
        rv_phone_numbers.layoutManager = LinearLayoutManager(this@AddContactActivity)

        emailAdapter = AddContactAdapter(this@AddContactActivity, arrayListOf())
        rv_email_addresses.adapter = emailAdapter
        rv_email_addresses.layoutManager = LinearLayoutManager(this@AddContactActivity)

        async(UI) {

            val cursor = bg {
                this@AddContactActivity.contentResolver?.query(contactUri, null, null, null, null)
            }.await()

            cursor?.moveToFirst()

            val name = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            val id = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

            Toast.makeText(this@AddContactActivity, "$name $id", Toast.LENGTH_SHORT).show()
            Log.d("HealthRelay","$name $id")

            val contactId = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

            val cr = this@AddContactActivity.contentResolver

            //get all phone information
            val phones = cr.query(Phone.CONTENT_URI, null,
                    Phone.CONTACT_ID + " = " + contactId, null, null)

            val phonesList = arrayListOf<String>()

            if(phones != null){
                while (phones.moveToNext()) {
                    val number = phones.getString(phones.getColumnIndex(Phone.NUMBER))
                    phonesList.add(number)
                    Log.d("HealthRelay",number)
                }

                phones.close()
            }

            phoneAdapter.contactInfos = phonesList
            phoneAdapter.notifyDataSetChanged()

            //get all email information
            val emails = cr?.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null)

            val emailsList = arrayListOf<String>()

            if(emails != null){
                while (emails.moveToNext()) {
                    val email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                    emailsList.add(email)
                    Log.d("HealthRelay",email)
                }

                emails.close()
            }

            emailAdapter.contactInfos = emailsList
            emailAdapter.notifyDataSetChanged()

            //and then initiate a new activity that lets the user choose which information they want to populate

            // Retrieve the phone number from the NUMBER column

            cursor?.close()

//                    bg{
//                        //if the name and number are not null, store into the db
//                        if(name != null && number != null && id != null){
//                            vm.addContact(id=id, name=name, number=number)
//                        }
      }
    }

}

