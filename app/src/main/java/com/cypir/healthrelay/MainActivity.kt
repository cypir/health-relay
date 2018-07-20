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
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

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
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                //message.setText(R.string.title_settings)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @AfterPermissionGranted(123)
    fun showStoredContacts(){
        val perms = arrayOf(Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS)

        val hasPermissions = EasyPermissions.hasPermissions(this,
                *perms)

        if (hasPermissions){
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, ContactFragment())
            ft.commit()
        }else{
            EasyPermissions.requestPermissions(this,
                    "We need read/write access to contacts",
                    123,
                    *perms
            )
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
