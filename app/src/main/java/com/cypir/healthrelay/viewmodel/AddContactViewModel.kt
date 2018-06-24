package com.cypir.healthrelay.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.database.Cursor
import android.provider.ContactsContract
import com.cypir.healthrelay.AppDatabase
import com.cypir.healthrelay.MainApplication
import com.cypir.healthrelay.entity.Contact
import com.cypir.healthrelay.entity.ContactInfo
import com.cypir.healthrelay.relation.ContactWithContactInfo
import kotlinx.android.synthetic.main.activity_add_contact.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.util.*
import javax.inject.Inject

/**
 * Created by wxz on 11/13/2017.
 */
class AddContactViewModel(application : Application) : AndroidViewModel(application) {
    @Inject
    lateinit var appDb : AppDatabase

    init {
        (application as MainApplication).injector.inject(this)
    }

    /**
     * adds a contact to the db. We need to add the contact along with their
     * information atomically.
     */
    fun saveContact(contactCursor : Cursor){
//        val contactInfo = ContactInfo(
//                contactId=contactId,
//                type=type,
//                info=info
//        )

        val name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
        val id = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID))

        async(UI){
            appDb.beginTransaction()




        }



        //TODO: throw error message if a duplicate add is attempted.
        //appDb.contactInfoDao().insertContactInfo(contactInfo)
    }

    /**
     * Gets the stored contact info for a particular contact
     */
    fun getStoredContactInfo(id: String) : ContactWithContactInfo {
        return appDb.contactDao().getContactWithContactInfoSync(id)
    }
}
