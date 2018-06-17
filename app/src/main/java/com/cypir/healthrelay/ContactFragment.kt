package com.cypir.healthrelay

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_contact.*
import android.provider.ContactsContract
import android.content.Intent
import android.widget.Toast
import android.R.attr.data
import android.app.Activity
import android.R.attr.data
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.R.attr.data
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.cypir.healthrelay.adapter.ContactAdapter
import com.cypir.healthrelay.entity.Contact
import com.cypir.healthrelay.viewmodel.MainViewModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import android.content.ContentResolver




/**
 * Displays the list of stored contacts.
 *
 */
class ContactFragment : Fragment() {

    private val PICK_CONTACT_REQUEST = 1

    lateinit var vm : MainViewModel

    //initialize adapter to empty
    lateinit var contactAdapter : ContactAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //initialize the view model
        vm = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactAdapter = ContactAdapter(context!!, arrayListOf())
        rv_contacts.adapter = contactAdapter
        rv_contacts.layoutManager = LinearLayoutManager(context)

        fab_add_contact.setOnClickListener { _ ->
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            pickContactIntent.type = ContactsContract.Contacts.CONTENT_TYPE // Show user only contacts w/ phone numbers
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST)
        }

        //create listener
        vm.contacts.observe(this, Observer<List<Contact>> {
            contacts ->
                Log.d("HealthRelay",contacts.toString())
                if(contacts != null){
                    contactAdapter.contacts = contacts

                    //TODO: use delta/patch diff tool
                    contactAdapter.notifyDataSetChanged()
                }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_CONTACT_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                val contactUri = data?.data
                // We only need the NUMBER column, because there will be only one row in the result
                val projection = arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID)

                async(UI) {

                    val cursor = bg {
                        activity?.contentResolver?.query(contactUri, null, null, null, null)
                    }.await()

                    cursor?.moveToFirst()

                    val name = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val id = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

                    Toast.makeText(activity, "$name $id", Toast.LENGTH_SHORT).show()
                    Log.d("HealthRelay","$name $id")

                    val contactId = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

                    val cr = activity?.contentResolver

                    //get all phone information
                    val phones = cr?.query(Phone.CONTENT_URI, null,
                            Phone.CONTACT_ID + " = " + contactId, null, null)

                    if(phones != null){
                        while (phones.moveToNext()) {
                            val number = phones.getString(phones.getColumnIndex(Phone.NUMBER))
                            Log.d("HealthRelay",number)
                        }

                        phones.close()
                    }

                    //get all email information
                    val emails = cr?.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null)

                    if(emails != null){
                        while (emails.moveToNext()) {
                            val email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                            Log.d("HealthRelay",email)
                        }

                        emails.close()
                    }

                    //and then initiate a new activity that lets the user choose which information they want to populate

                    // Retrieve the phone number from the NUMBER column

                    cursor?.close()

//                    bg{
//                        //if the name and number are not null, store into the db
//                        if(name != null && number != null && id != null){
//                            vm.addContact(id=id, name=name, number=number)
//                        }
//                    }
                }
            }
        }
    }


}
