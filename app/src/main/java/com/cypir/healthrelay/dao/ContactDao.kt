package com.cypir.healthrelay.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.cypir.healthrelay.entity.Contact
import com.cypir.healthrelay.relation.ContactWithContactData

/**
 * Created by wxz on 11/12/2017.
 */
@Dao
interface ContactDao {
    @Transaction
    @Query("SELECT * from Contact")
    fun getContacts() : LiveData<List<Contact>>

    @Transaction
    @Query("SELECT * from Contact")
    fun getContactsSync() : List<Contact>

    @Transaction
    @Query("SELECT * from Contact where id = :id")
    fun getContactSync(id : String) : Contact

    @Transaction
    @Query("SELECT * from Contact where id = :id")
    fun getContactWithContactDataSync(id : String) : ContactWithContactData

    //if we are trying to insert contact data with an ID that already exists, then ignore it.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertContact(contact : Contact) : Long
}
