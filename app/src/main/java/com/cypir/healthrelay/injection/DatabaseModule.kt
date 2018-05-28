package com.cypir.healthrelay.injection

import android.app.Application
import android.arch.persistence.room.Room
import com.cypir.healthrelay.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wxz on 7/24/2017.
 */
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDb(app: Application) : AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "app.db").build()
    }
}
