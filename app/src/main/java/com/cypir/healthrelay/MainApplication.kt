package com.cypir.healthrelay

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.cypir.healthrelay.injection.AppModule
import com.cypir.healthrelay.injection.DaggerInjectorComponent
import com.cypir.healthrelay.injection.DatabaseModule
import com.cypir.healthrelay.injection.InjectorComponent
import com.facebook.stetho.Stetho

class MainApplication : Application() {
    lateinit var injector: InjectorComponent

    override fun onCreate() {
        super.onCreate()

        injector = DaggerInjectorComponent.builder()
                .appModule(AppModule(this))
                .databaseModule(DatabaseModule())
                .build()

        Stetho.initializeWithDefaults(this)

        if(Build.VERSION.SDK_INT >= 26) {
            //initialize notification channel
            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // The id of the channel.
            val id = "health_relay_channel"
            // The user-visible name of the channel.
            val name = getString(R.string.channel_name)
            // The user-visible description of the channel.
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name, importance)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.enableLights(false)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(false)
            mNotificationManager.createNotificationChannel(mChannel)
        }

    }
}