package com.cypir.healthrelay.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.*
import com.cypir.healthrelay.AppDatabase
import javax.inject.Inject
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.text.format.DateUtils
import android.util.Log
import com.cypir.healthrelay.MainActivity
import com.cypir.healthrelay.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.os.*
import android.support.v7.preference.PreferenceManager
import android.telephony.SmsManager


/**
 * Foreground service that monitors for screen on and off to determine if a user is idle or not. Uses
 * setExactAndAllowWhileIdle to ensure that message gets delivered at the proper time.
 */
class RelayService : Service() {
    @Inject
    lateinit var app : Application

    private val NOTIFICATION_ID = 1
    val CHANNEL_ID = "health_relay_channel"

    private lateinit var notificationManager : NotificationManagerCompat


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(application, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 775, alarmIntent, 0)

        //receiver for when the screen is turned off.
        //When screen is turned off, schedule an alarm x minutes into the future, where x
        //is the interval that we set.
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //when the screen turns off,
                //am.setExactAndAllowWhileIdle()
                Log.d("HealthRelay","The screen is off")

                //set the interval timer once the screen turns off.

                val defaultInterval = resources.getString(R.string.interval_default)
                val intervalKey = resources.getString(R.string.interval_key)
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val intervalMins = sharedPreferences.getString(intervalKey, defaultInterval).toInt()


                Log.d("HealthRelay","Wait for $intervalMins mins...")
                Log.d("HealthRelay",Date().toString())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d("HealthRelay","Setting alarm M")
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + 1000 * 60 * intervalMins, pendingIntent)
                }else{
                    Log.d("HealthRelay","Setting alarm < M")
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + 1000 * 60 * intervalMins, pendingIntent)
                }

            }
        }, IntentFilter(Intent.ACTION_SCREEN_OFF))

        //Receiver for when the screen is turned on.
        //When the screen is turned on, cancel the alarm that we scheduled when the screen turns on

        registerReceiver(object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                //This happens when the screen is turned on and screen lock deactivated
                Log.d("HealthRelay","Screen has come back on")

                //we need to cancel the alarm
                alarmManager.cancel(pendingIntent)
            }
        }, IntentFilter(Intent.ACTION_SCREEN_ON))



        notificationManager =  NotificationManagerCompat.from(this)

        startForeground(NOTIFICATION_ID, createNotification())

        return Service.START_STICKY
    }

    private fun createNotification() : Notification {
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_record_voice_over_black_24dp)
                .setContentTitle("Health Relay")
                .setContentText("Health Relay Active")

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        mBuilder.setContentIntent(pendingIntent)

        return mBuilder.build()
    }

    /**
     * Update should show last time a notification was sent out
     */
    private fun updateNotification() {

//        val intent = Intent(this, MainActivity::class.java)
//        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        // resultIntent.putExtra("coachingSessionId",session.id)
//
//        //PendingIntent.FLAG_CANCEL_CURRENT resumes activity
////        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
////                PendingIntent.FLAG_ONE_SHOT)
//
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_record_voice_over_black_24dp)
//                .setContentTitle("Health Relay")
//                .setContentText("Iterations: $iterations Last Reset: $lastReset")
//                //.setContentIntent(pendingIntent)
//
//        notificationManager.notify(
//                NOTIFICATION_ID,
//                builder.build())
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //TODO on service terminaltion, unregister listener

}