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
import android.net.Uri


/**
 * Displays the list of stored contacts.
 *
 */
class ContactFragment : Fragment() {

    val PICK_CONTACT_REQUEST = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_add_contact.setOnClickListener { _ ->
            val pickContactIntent = Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"))
            pickContactIntent.type = Phone.CONTENT_TYPE // Show user only contacts w/ phone numbers
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_CONTACT_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                val contactUri = data?.data
                // We only need the NUMBER column, because there will be only one row in the result
                val projection = arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER)

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                val cursor = activity?.contentResolver?.query(contactUri, projection, null, null, null)
                cursor?.moveToFirst()

                // Retrieve the phone number from the NUMBER column
                val column = cursor?.getColumnIndex(Phone.NUMBER)
                val number = cursor?.getString(column!!)

                val name = cursor?.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME))

                Toast.makeText(activity, "$number $name", Toast.LENGTH_SHORT).show()

                cursor?.close()
            }
//            val contactData = data?.data
//            val c = getContentResolver().query(contactData, null, null, null, null)
//            if (c.moveToFirst()) {
//                val phoneIndex = getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//                val num = c.getString(phoneIndex)
//                Toast.makeText(this@MainActivity, "Number=$num", Toast.LENGTH_LONG).show()
//            }
        }
    }


}
