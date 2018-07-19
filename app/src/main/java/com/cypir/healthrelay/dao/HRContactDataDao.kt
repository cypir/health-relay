package com.cypir.healthrelay.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.cypir.healthrelay.entity.HRContactData

/**
 * Created by wxz on 11/12/2017.
 */
@Dao
interface HRContactDataDao {

    @Insert
    fun insertContactDatum(HRContactData : HRContactData)

    //when we try to insert, replace on conflict. possibly create non persistent to determine if update or insert?
    //TODO convert this to upsert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContactData(list : List<HRContactData>)

    @Query("select * from Contact_Data where raw_contact_id = :rawContactId")
    fun getHRContactDataByContactId(rawContactId : String) : List<HRContactData> ?
}
