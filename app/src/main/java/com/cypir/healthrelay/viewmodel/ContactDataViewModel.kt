package com.cypir.healthrelay.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.net.Uri
import android.util.Log
import com.cypir.healthrelay.AppDatabase
import com.cypir.healthrelay.MainApplication
import com.cypir.healthrelay.entity.HRContactData
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import javax.inject.Inject
import kotlinx.coroutines.experimental.launch


/**
 * Created by wxz on 11/13/2017.
 */
class ContactDataViewModel(application : Application) : AndroidViewModel(application) {
    @Inject
    lateinit var appDb : AppDatabase
    var contactId : Long = -1
    lateinit var contactName : String
    lateinit var contactUri : Uri

    //this is the health relay data id for the MIMETYPE_HRNOTIFY row.
    var hrMimeId : String? = null

    val MIMETYPE_HRNOTIFY = "vnd.android.cursor.item/health_relay_notify"

    init {
        (application as MainApplication).injector.inject(this)
    }

    /**
     * Store DATA level data for HR, such as enable/disable notifications
     */
    fun saveHRContactData(list : List<HRContactData>){

        //val values = ContentValues()
        //values.put(ContactsContract.Data.DATA1, if (this.getFormality()) "1" else "0")

        //get only isEnabled contacts to save
        //val toInsert = list.filter { it.isEnabled }

        //TODO make this a transaction and and upsert
        launch(UI){
            bg {
                //insert only contacts that are enabled
                appDb.hrContactDataDao().insertHRContactData(list)
            }.await()
        }
    }

    fun removeHrContactData(list : List<Long>) : Deferred<Any>{
        return async(UI){
            bg {appDb.hrContactDataDao().deleteHRContactData(list)}.await()
        }

    }

    /**
     * Gets the stored contact info for a particular contact
     */
    fun getHRContactDataByRawContactId(id: Long) : List<HRContactData>?{

        return try{
            appDb.hrContactDataDao().getHRContactDataByRawContactId(id)
        }catch(ex : Exception){
            Log.d("HealthRelay","Ran into an error")
            null
        }

    }
}
