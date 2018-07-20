package com.cypir.healthrelay

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cypir.healthrelay.adapter.ContactAdapter
import com.cypir.healthrelay.entity.HRContactData
import com.cypir.healthrelay.pojo.HRContact
import com.cypir.healthrelay.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg


/**
 * Displays the list of stored contacts.
 *
 */
class ContactFragment : Fragment(), ContactAdapter.OnItemClickListener {
    override fun onItemClick(item: HRContact) {
        //launch the ContactDataActivity with the appropriate contact uri
        val intent = Intent(context, ContactDataActivity::class.java)
        intent.putExtra("contactUri", ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, item.contactId.toLong())
        )
        startActivity(intent)
    }

    private val PICK_CONTACT_REQUEST = 1
    private val HR_NOTIFY_MIMETYPE = "vnd.android.cursor.item/health_relay_notify"

    lateinit var vm: MainViewModel

    //initialize adapter to empty
    lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //initialize the view model
        vm = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactAdapter = ContactAdapter(context!!, arrayListOf(), this)
        rv_contacts.adapter = contactAdapter
        rv_contacts.layoutManager = LinearLayoutManager(context)

        //create observer on HRContactData in order to update our contact list whenever we create/update/delete
        //HRContactData (eg. adding a contact data selection

        vm.getHRContactData().observe(this, Observer<List<HRContactData>> {
            launch(UI) {
                try {
                    val c = bg {

                        activity?.contentResolver?.query(
                                Data.CONTENT_URI,
                                arrayOf(
                                        Data.RAW_CONTACT_ID,
                                        Data.CONTACT_ID,
                                        Data._ID,
                                        StructuredName.DISPLAY_NAME_PRIMARY
                                ),
                                Data.MIMETYPE + "=?", arrayOf(HR_NOTIFY_MIMETYPE), null
                        )
                    }.await()

                    val hrContacts = arrayListOf<HRContact>()

                    //iterate through data that has the appropriate mimetype and group by the contact_id.
                    if (c != null) {
                        while (c.moveToNext()) {
                            //find if we already have this contact id in our list of contact names
                            val indexOfContactId = hrContacts.indexOfFirst {
                                it.contactId == c.getString(c.getColumnIndex(Data.CONTACT_ID))
                            }

                            //only add contact name if index is 0
                            if (indexOfContactId < 0) {
                                hrContacts.add(
                                        HRContact(
                                                rawContactId = c.getString(c.getColumnIndex(Data.RAW_CONTACT_ID)),
                                                displayName = c.getString(c.getColumnIndex(StructuredName.DISPLAY_NAME_PRIMARY)),
                                                contactDataId = c.getString(c.getColumnIndex(Data._ID)),
                                                contactId = c.getString(c.getColumnIndex(Data.CONTACT_ID))
                                        )
                                )
                            }
                        }
                    }

                    //close the cursor
                    c?.close()

                    //TODO do a diff patch to get differences
                    contactAdapter.contacts = hrContacts
                    contactAdapter.notifyDataSetChanged()

                } catch (ex: Exception) {
                    Log.d("HealthRelayError", ex.toString())
                }
            }
        })

        fab_add_contact.setOnClickListener { _ ->
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            pickContactIntent.type = ContactsContract.Contacts.CONTENT_TYPE // Show user only contacts w/ phone numbers
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST)
        }

        //create listener
        /**
         * Create listener for content provider (content observer?) that will update the contact list
         * when a change occurs.
         */
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                //when we receive the response from the contact picker activity, we then initiate our own
                //dialog which allows the user to select the particular contact information they want for this user.
                val intent = Intent(context, ContactDataActivity::class.java)
                intent.putExtra("contactUri", data?.data)
                startActivity(intent)
            }
        }
    }


}
