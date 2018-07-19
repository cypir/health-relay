package com.cypir.healthrelay.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import com.cypir.healthrelay.AppDatabase
import com.cypir.healthrelay.MainApplication
import com.cypir.healthrelay.entity.HRContactData
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.coroutines.experimental.bg
import javax.inject.Inject
import kotlinx.coroutines.experimental.launch


/**
 * Created by wxz on 11/13/2017.
 */
class ContactDataViewModel(application : Application) : AndroidViewModel(application) {
    @Inject
    lateinit var appDb : AppDatabase
    lateinit var contactId : String
    lateinit var contactName : String

    val MIMETYPE_HRNOTIFY = "vnd.android.cursor.item/health_relay_notify"

    init {
        (application as MainApplication).injector.inject(this)
    }

    /**
     * uses a custom MIMETYPE to store the relevant notify boolean (whether or not to notify
     * a particular data set). We only have to update the ContactContract.Data table.
     * We will query the data table for all entries with the custom mimetype and manually
     * group by lookupid
     */
    fun saveContact(contactId: String, list : List<HRContactData>){

        //val values = ContentValues()
        //values.put(ContactsContract.Data.DATA1, if (this.getFormality()) "1" else "0")

        //get only isEnabled contacts to save
        val toInsert = list.filter { it.isEnabled }

        //TODO make this a transaction and and upsert
        launch(UI){
            //bg { appDb.contactDao().insertContact(Contact(id = contactId, name=contactName)) }.await()
            bg {
                //insert only contacts that are enabled
                appDb.contactDataDao().insertContactData(toInsert)
            }.await()
        }
    }

    /**
     * Gets the stored contact info for a particular contact
     */
    fun getHRContactData(id: String) : List<HRContactData>?{
        val list : List<HRContactData>? = try{
            appDb.contactDataDao().getHRContactDataByContactId(id)
        }catch(ex : Exception){
            Log.d("HealthRelay","Ran into an error")
            null
        }

        return list

    }
}
