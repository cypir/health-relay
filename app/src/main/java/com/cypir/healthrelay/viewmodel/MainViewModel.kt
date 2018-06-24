package com.cypir.healthrelay.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.cypir.healthrelay.AppDatabase
import com.cypir.healthrelay.MainApplication
import com.cypir.healthrelay.entity.Contact
import java.util.*
import javax.inject.Inject

/**
 * Created by wxz on 11/13/2017.
 */
class MainViewModel(application : Application) : AndroidViewModel(application) {
    @Inject
    lateinit var appDb : AppDatabase

    val contacts : LiveData<List<Contact>>
    val nextCheck: Date

    var status = "Active"

    init {
        (application as MainApplication).injector.inject(this)
        contacts = appDb.contactDao().getContacts()

        //initialize the next check for the date to
        nextCheck = Date()
    }

    /**
     * adds a contact to the db
     */
    fun addContact(id: String, name : String, number: String){
        val contact = Contact(
                id=id,
                name=name
        )

        //TODO: throw error message if a duplicate add is attempted.
        appDb.contactDao().insertContact(contact)
    }
}
