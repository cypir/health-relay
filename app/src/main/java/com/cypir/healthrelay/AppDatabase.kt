package com.cypir.healthrelay

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.cypir.healthrelay.dao.HRContactDataDao
import com.cypir.healthrelay.entity.HRContactData

/**
 * Created by Alex Nguyen on 11/10/2017.
 */
@Database(entities = [HRContactData::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun hrContactDataDao() : HRContactDataDao
}
