package com.cypir.healthrelay.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.cypir.healthrelay.entity.Contact

/**
 * Created by wxz on 11/12/2017.
 */
@Dao
interface ContactDao {
    @Transaction
    @Query("SELECT * from Contact where enabled = 1")
    fun getEnabledContacts() : LiveData<List<Contact>>

    @Transaction
    @Query("SELECT * from Contact")
    fun getContactsSync() : List<Contact>

    @Insert
    fun insertContact(contact : Contact) : Long
}
