package com.cypir.healthrelay

import android.app.Application
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
    }
}