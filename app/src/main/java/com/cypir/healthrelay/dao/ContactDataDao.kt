package com.cypir.healthrelay.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import com.cypir.healthrelay.entity.ContactData

/**
 * Created by wxz on 11/12/2017.
 */
@Dao
interface ContactDataDao {

    @Insert
    fun insertContactDatum(contactData : ContactData)

    //when we try to insert, replace on conflict. possibly create non persistent to determine if update or insert?
    //TODO convert this to upsert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContactData(list : List<ContactData>)
}
