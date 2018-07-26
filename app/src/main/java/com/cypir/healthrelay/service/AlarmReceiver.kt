package com.cypir.healthrelay.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.Application
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.provider.ContactsContract.Data
import android.support.v7.preference.PreferenceManager
import android.telephony.SmsManager
import android.util.Log
import com.cypir.healthrelay.AppDatabase
import com.cypir.healthrelay.MainApplication
import com.cypir.healthrelay.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*
import javax.inject.Inject


/**
 * Receives the alarm responsible for triggering contact notifications.
 * Iterates through all contact data rows and sends the notification.
 */
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var appDb : AppDatabase

    override fun onReceive(context: Context, intent: Intent?) {
        (context.applicationContext as MainApplication).injector.inject(this)

        Log.d("HealthRelay","Initiating notification sending...")
        Log.d("HealthRelay", Date().toString())

        launch(UI){
            val contactData = bg{ appDb.hrContactDataDao().getAllEnabledHRContactDataIdsSync() }.await()
            val contactDataStr = contactData.map { it.toString() }

            //count how many contact data points we have. This is the number of ? we need for the selection
            //params

            var idString = ""

            contactData.forEach { idString += "?," }
            idString = idString.dropLast(1)

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val notificationDisabled = sharedPreferences.getBoolean(
                    context.resources.getString(R.string.disable_notifications_key),
                    context.resources.getBoolean(R.bool.disable_notifications_default))

            if(notificationDisabled){
                Log.w("HealthRelay","WARNING: Notifications are disabled")
            }

            Log.d("HealthRelay",contactData.toString())

            //get existing list of contact methods
            val cr = context.contentResolver
            val uri = Data.CONTENT_URI

            Log.d("HealthRelay",Data._ID + " IN (" + idString + ")")

            val c = cr.query(uri, arrayOf
            (
                    Data.DATA1,
                    Data.MIMETYPE
            ),
                    Data._ID + " IN (" + idString + ")",
                    contactDataStr.toTypedArray(), null)

            while(c.moveToNext()){
                val data1 = c.getString(c.getColumnIndex(Data.DATA1))
                val mimetype =  c.getString(c.getColumnIndex(Data.MIMETYPE))

                //if the mimetype is the phone type, we send SMS
                when(mimetype) {
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                        //all ids in contactData are enabled already, so we go ahead and send the SMS
                        Log.d("HealthRelay","SMS to $data1")

                        //if notifications are not disabled
                        if(!notificationDisabled){
                            if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                                val smsManager = SmsManager.getDefault()

                                smsManager.sendTextMessage(data1, null, "Hello from health relay",
                                        null, null)
                            }
                        }

                    }
                }

            }

            c.close()
        }
    }
}