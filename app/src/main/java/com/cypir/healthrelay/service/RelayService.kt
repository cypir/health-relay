package com.cypir.healthrelay.service

import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.*
import android.os.IBinder
import com.cypir.healthrelay.AppDatabase
import javax.inject.Inject
import android.os.CountDownTimer
import android.support.v4.app.NotificationCompat
import android.text.format.DateUtils
import android.util.Log
import com.cypir.healthrelay.MainActivity


/**
 * Foreground service that checks the accelerometer every minute for 10 seconds to see if
 * any movement is happening.
 *
 * 1. If movement is happening, reset the time to send the SMS by + interval.
 * 2. If movement is not happening, do nothing.
 *
 * If the time to send SMS message is hit or passed, then we send the SMS message.
 *
 * 1. Get contacts via room and then send SMS to each one of those contacts.
 */
class RelayService : Service(), SensorEventListener {
    @Inject
    lateinit var appDb : AppDatabase

    @Inject
    lateinit var app : Application

    lateinit var sensorManager : SensorManager
    lateinit var sensor : Sensor

    private val NOTIFICATION_ID = 1
    val CHANNEL_ID = "health_relay_channel"

    private var timer : CountDownTimer? = null
    private var msRemaining = 0L

    //the interval is minutes to MS
    private val interval = 30 * 60 * 1000L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //return super.onStartCommand(intent, flags, startId)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(this, sensor, 2 * 1000 * 1000)

        timer = initTimer(timer, interval)

        startForeground(NOTIFICATION_ID, createNotification())

        return Service.START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //if any values on gyroscope are not equal to 0 (means moving)
        if(event!!.values[0] != 0f || event.values[1] != 0f || event.values[2] != 0f){
            timer = initTimer(timer, interval)
        }

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //cancel existing timer if necessary and recreate the timer
    private fun initTimer(timer: CountDownTimer?, interval : Long): CountDownTimer{
        timer?.cancel()

        return object : CountDownTimer(interval, 250) {
            override fun onFinish() {
                //update last tick
                msRemaining = 0

                //send SMS
                Log.d("HealthRelay","The timer has finished and we will send an SMS")

                //updateNotification()
            }

            //on tick purely meant for showing the countdown timer
            override fun onTick(ms: Long) {
                msRemaining = ms
                Log.d("HealthRelay",DateUtils.formatElapsedTime(ms / 1000) + " remaining")
                //updateNotification()
            }
        }.start()
    }


    private fun createNotification() : Notification {
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Health Relay")
                .setContentText("Health Relay Active")

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        // resultIntent.putExtra("coachingSessionId",session.id)

        //PendingIntent.FLAG_CANCEL_CURRENT resumes activity
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)

        mBuilder.setContentIntent(pendingIntent)

        return mBuilder.build()
    }

//    private fun updateNotification() {
//
//        //start with where we currently are
//        var totalRemainingTime = msRemaining.toFloat()/1000
//
//        //go through the rest of the timeslots
//        totalRemainingTime += (timeslotIndex + 1 until programs[programIndex].timeslots.size)
//                .map { programs[programIndex].timeslots[it].duration }
//                .sum()
//
//        //go through the next set of programs
//        for(i in programIndex + 1 until programs.size){
//            //and all their timeslots
//            for(k in 0 until programs[i].timeslots.size){
//                //and add to remaining time
//                totalRemainingTime += programs[i].timeslots[k].duration
//            }
//        }
//
//        val intent = Intent(this, CoachingSessionActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        // resultIntent.putExtra("coachingSessionId",session.id)
//
//        //PendingIntent.FLAG_CANCEL_CURRENT resumes activity
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT)
//
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_access_time_black_24dp)
//                .setContentTitle("Coaching Time")
//                .setContentText("Time Remaining: " + DateUtils.formatElapsedTime(totalRemainingTime.toLong()))
//                .setContentIntent(pendingIntent)
//
//        notificationManager.notify(
//                NOTIFICATION_ID,
//                builder.build())
//    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //TODO on service terminaltion, unregister listener

}