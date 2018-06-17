package com.cypir.healthrelay

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.cypir.healthrelay.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.os.Build
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.cypir.healthrelay.service.RelayService
import permissions.dispatcher.*

class MainActivity : AppCompatActivity() {

    lateinit var vm : MainViewModel

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_container, SummaryFragment())
                ft.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contacts -> {
                //switch to user fragment
                showStoredContacts()
                //showContactsWithPermissionCheck()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                //message.setText(R.string.title_settings)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun showStoredContacts() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, ContactFragment())
        ft.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize the view model
        vm = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // default the fragment to the summary
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SummaryFragment())
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
