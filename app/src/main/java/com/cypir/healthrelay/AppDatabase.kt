package com.cypir.healthrelay

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.cypir.healthrelay.dao.ContactDao
import com.cypir.healthrelay.entity.Contact

/**
 * Created by Alex Nguyen on 11/10/2017.
 */
@Database(entities = [(Contact::class)], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun ContactDao(): ContactDao
}
