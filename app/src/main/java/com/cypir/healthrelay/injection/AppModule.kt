package com.cypir.healthrelay.injection

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Alex Nguyen on 7/24/2017.
 */

@Module
class AppModule(var application : Application) {
    @Provides
    @Singleton
    fun provideApplication() : Application {
        return application
    }
}
