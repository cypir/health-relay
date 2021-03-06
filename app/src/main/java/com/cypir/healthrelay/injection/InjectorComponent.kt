package com.cypir.healthrelay.injection

import android.content.BroadcastReceiver
import com.cypir.healthrelay.service.AlarmReceiver
import com.cypir.healthrelay.service.RelayService
import com.cypir.healthrelay.viewmodel.ContactDataViewModel
import com.cypir.healthrelay.viewmodel.MainViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Alex Nguyen on 11/12/2017.
 */

@Singleton
@Component(
        modules = [(AppModule::class), (DatabaseModule::class)]
)
interface InjectorComponent {
        fun inject(vm : MainViewModel)
        fun inject(service : RelayService)
        fun inject(vm : ContactDataViewModel)
        fun inject(b : AlarmReceiver)
}
