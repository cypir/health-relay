package com.cypir.healthrelay.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.Application
import android.provider.ContactsContract
import android.util.Log
import com.cypir.healthrelay.AppDatabase
import com.cypir.healthrelay.MainApplication
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
            val contactData = bg{ appDb.hrContactDataDao().getAllHRContactDataSync() }.await()

            Log.d("HealthRelay",contactData.toString())

            //go through each contact and send an sms
            //TODO account for if interval is empty
            contactData.forEach {
                when(it.mimetype){
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> Log.d("HealthRelay",it.id.toString())
                }
            }
        }
    }
}