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

    val enabledContacts : LiveData<List<Contact>>
    val nextCheck: Date

    var status = "Active"

    init {
        (application as MainApplication).injector.inject(this)
        enabledContacts = appDb.ContactDao().getEnabledContacts()

        //initialize the next check for the date to
        nextCheck = Date()
    }
}
