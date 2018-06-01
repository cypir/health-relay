package com.cypir.healthrelay

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.cypir.healthrelay.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.os.Build
import com.cypir.healthrelay.service.RelayService


class MainActivity : AppCompatActivity() {

    lateinit var vm : MainViewModel



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                //message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contacts -> {
                //message.setText(R.string.title_contacts)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                //message.setText(R.string.title_settings)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize the view model
        vm = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // default the fragment to the summary
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fragment_container, SummaryFragment())
        ft.commit()

        //initialize the relay service
        val intent = Intent(this, RelayService::class.java)

        //start foreground service from android o
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
