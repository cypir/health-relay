package com.cypir.healthrelay

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.cypir.healthrelay.dao.ContactDao
import com.cypir.healthrelay.dao.ContactInfoDao
import com.cypir.healthrelay.entity.Contact
import com.cypir.healthrelay.entity.ContactInfo

/**
 * Created by Alex Nguyen on 11/10/2017.
 */
@Database(entities = [Contact::class, ContactInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun contactDao(): ContactDao
    abstract fun contactInfoDao() : ContactInfoDao
}
