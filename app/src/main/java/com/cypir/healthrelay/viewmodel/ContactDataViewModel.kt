package com.cypir.healthrelay.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.database.Cursor
import android.provider.ContactsContract
import com.cypir.healthrelay.AppDatabase
import com.cypir.healthrelay.MainApplication
import com.cypir.healthrelay.entity.Contact
import com.cypir.healthrelay.entity.ContactData
import com.cypir.healthrelay.relation.ContactWithContactData
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import javax.inject.Inject

/**
 * Created by wxz on 11/13/2017.
 */
class ContactDataViewModel(application : Application) : AndroidViewModel(application) {
    @Inject
    lateinit var appDb : AppDatabase
    lateinit var contactId : String
    lateinit var contactName : String

    init {
        (application as MainApplication).injector.inject(this)
    }

    /**
     * adds a contact to the db. We need to add the contact along with their
     * information atomically.
     */
    fun saveContact(list : List<ContactData>){

        //TODO make this a transaction and and upsert
        async(UI){
            bg { appDb.contactDao().insertContact(Contact(id = contactId, name=contactName)) }.await()
            bg {
                appDb.contactDataDao().insertContactData(list)
            }.await()
        }

    }

    /**
     * Gets the stored contact info for a particular contact
     */
    fun getStoredContactInfo(id: String) : ContactWithContactData? {
        return appDb.contactDao().getContactWithContactDataSync(id)
    }
}
