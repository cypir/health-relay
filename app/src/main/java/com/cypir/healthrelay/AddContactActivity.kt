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
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.cypir.healthrelay.adapter.AddContactAdapter
import com.cypir.healthrelay.adapter.ContactAdapter
import com.cypir.healthrelay.viewmodel.AddContactViewModel
import kotlinx.android.synthetic.main.activity_add_contact.*
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import android.view.MenuInflater
import com.cypir.healthrelay.entity.ContactInfo


class AddContactActivity : AppCompatActivity() {

    lateinit var phoneAdapter : AddContactAdapter
    lateinit var emailAdapter : AddContactAdapter

    lateinit var vm : AddContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        vm = ViewModelProviders.of(this).get(AddContactViewModel::class.java)

        val extras = intent.extras
        val contactUri = extras.getParcelable<Uri>("contactUri")

        //initialize adapters
        phoneAdapter = AddContactAdapter(this@AddContactActivity, arrayListOf())
        rv_phone_numbers.adapter = phoneAdapter
        rv_phone_numbers.layoutManager = LinearLayoutManager(this@AddContactActivity)

        emailAdapter = AddContactAdapter(this@AddContactActivity, arrayListOf())
        rv_email_addresses.adapter = emailAdapter
        rv_email_addresses.layoutManager = LinearLayoutManager(this@AddContactActivity)

        text_name.text = ""

        async(UI) {

            val cursor = bg {
                this@AddContactActivity.contentResolver?.query(contactUri, null, null, null, null)
            }.await()

            cursor?.moveToFirst()

            val name = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            text_name.text = name

            val contactId = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

            //get existing list of contact methods
            val cr = this@AddContactActivity.contentResolver

            setAdapterList(Phone.CONTENT_URI, cr, Phone.NUMBER, Phone._ID, Phone.CONTACT_ID, contactId!!, "phone", phoneAdapter)
            setAdapterList(Email.CONTENT_URI, cr, Email.ADDRESS, Email._ID, Email.CONTACT_ID, contactId, "email", emailAdapter)



            cursor?.close()



//                    bg{
//                        //if the name and number are not null, store into the db
//                        if(name != null && number != null && id != null){
//                            vm.addContact(id=id, name=name, number=number)
//                        }
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

            //get selected
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun setAdapterList(uri : Uri, cr: ContentResolver, columnIndex : String, contactInfoIdCol: String, contactIdCol: String, contactId : String, contactInfoType: String, adapter : AddContactAdapter) {
        val info = cr.query(uri, null,
                "$contactIdCol = $contactId", null, null)

        //list of phone numbers, list of emails, etc go in here
        val infoList = arrayListOf<ContactInfo>()

        //hide the contact information if it was already used
        async(UI) {
            val contactWithContactInfo = bg { vm.getStoredContactInfo(contactId) }.await()

            if(info != null){
                while (info.moveToNext()) {
                    //TODO: possibly check to see if _ID of col info has changed for duplicate info?
                    val contactInfo = info.getString(info.getColumnIndex(columnIndex))
                    val id = info.getString(info.getColumnIndex(contactInfoIdCol))
                    infoList.add(ContactInfo(id=id, type=contactInfoType, info=contactInfo, ))
                    Log.d("HealthRelay",infoToAdd)
                }

                info.close()
            }

            adapter.contactInfo = infoList
            adapter.initialize()
            adapter.notifyDataSetChanged()
        }


    }

}

