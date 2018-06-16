package com.cypir.healthrelay


import android.Manifest
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.fragment_contact.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ContactFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private val PROJECTION = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    )

    // The column index for the _ID column
    private val CONTACT_ID_INDEX = 0
    // The column index for the LOOKUP_KEY column
    private val LOOKUP_KEY_INDEX = 1

    // Define variables for the contact the user selects
    // The contact's _ID value
    var mContactId: Long = 0
    // The contact's LOOKUP_KEY
    var mContactKey: String? = null
    // A content URI for the selected contact
    var mContactUri: Uri? = null
    // An adapter that binds the result Cursor to the ListView
    private var mCursorAdapter: SimpleCursorAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupCursorAdapter()

        contact_list.onItemClickListener = this
        loaderManager.initLoader(0, null, this);
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Get the Cursor
        val cursor = (parent?.adapter as SimpleCursorAdapter).cursor
        // Move to the selected contact
        cursor.moveToPosition(position)
        // Get the _ID value
        mContactId = cursor.getLong(CONTACT_ID_INDEX)
        // Get the selected LOOKUP KEY
        mContactKey = cursor.getString(LOOKUP_KEY_INDEX)
        // Create the contact's content Uri
        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey)
        /*
         * You can use mContactUri as the content URI for retrieving
         * the details for a contact.
         */
    }

    private fun setupCursorAdapter() {
        // Column data from cursor to bind views from
        val uiBindFrom = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.DISPLAY_NAME)
        // View IDs which will have the respective column data inserted
        val uiBindTo = intArrayOf(R.id.text_name, R.id.text_number)
        // Create the simple cursor adapter to use for our list
        // specifying the template to inflate (item_contact),
        mCursorAdapter = SimpleCursorAdapter(
                activity, R.layout.item_contact,
                null, uiBindFrom, uiBindTo,
                0)
        contact_list.adapter = mCursorAdapter
    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
        /*
         * return all contacts
         */
        // Starts the query
        return CursorLoader(
                activity!!,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter?.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Delete the reference to the existing Cursor
        mCursorAdapter?.swapCursor(null)

    }

}
