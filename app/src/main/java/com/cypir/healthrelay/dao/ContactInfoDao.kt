package com.cypir.healthrelay.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.cypir.healthrelay.entity.Contact
import com.cypir.healthrelay.entity.ContactInfo
import com.cypir.healthrelay.relation.ContactWithContactInfo

/**
 * Created by wxz on 11/12/2017.
 */
@Dao
interface ContactInfoDao {

    @Insert
    fun insertContactInfo(contactInfo : ContactInfo)
}
