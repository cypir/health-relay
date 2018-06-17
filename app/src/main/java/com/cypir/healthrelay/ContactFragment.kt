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
            val pickContactIntent = Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"))
            pickContactIntent.type = Phone.CONTENT_TYPE // Show user only contacts w/ phone numbers
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
                        activity?.contentResolver?.query(contactUri, projection, null, null, null)
                    }.await()

                    cursor?.moveToFirst()

                    // Retrieve the phone number from the NUMBER column
                    val column = cursor?.getColumnIndex(Phone.NUMBER)
                    val number = cursor?.getString(column!!)

                    val name = cursor?.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME))
                    val id = cursor?.getString(cursor.getColumnIndex(Phone.CONTACT_ID))

                    Toast.makeText(activity, "$number $name", Toast.LENGTH_SHORT).show()

                    cursor?.close()

                    bg{

                        //if the name and number are not null, store into the db
                        if(name != null && number != null && id != null){
                            vm.addContact(id=id, name=name, number=number)
                        }

                    }
                }


            }
        }
    }


}
