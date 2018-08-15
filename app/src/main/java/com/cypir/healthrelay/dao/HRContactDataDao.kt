package com.cypir.healthrelay.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.cypir.healthrelay.entity.HRContactData

/**
 * Created by wxz on 11/12/2017.
 */
@Dao
interface HRContactDataDao {

    //when we try to insert, replace on conflict. possibly create non persistent to determine if update or insert?
    //TODO convert this to upsert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHRContactData(list : List<HRContactData>)

    @Query("select * from HR_Contact_Data where raw_contact_id = :rawContactId")
    fun getHRContactDataByRawContactId(rawContactId : Long) : List<HRContactData> ?

    @Query("select * from HR_Contact_Data")
    fun getAllHRContactData() : LiveData<List<HRContactData>>

    @Query("select * from HR_Contact_Data")
    fun getAllHRContactDataSync() : List<HRContactData>

    @Query("select id from HR_Contact_Data where isEnabled = 1")
    fun getAllEnabledHRContactDataIdsSync() : List<Long>

    //deletes from HRContactData where the raw_contact_id matches the ids in the list
    @Query("delete from HR_Contact_Data where raw_contact_id IN (:list)")
    fun deleteHRContactData(list : List<Long>)

}
