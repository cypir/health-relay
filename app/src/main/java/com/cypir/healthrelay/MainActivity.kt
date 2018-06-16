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

@RuntimePermissions
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
                showContactsWithPermissionCheck()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                //message.setText(R.string.title_settings)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun showContacts(){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, ContactFragment())
        ft.commit()
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    fun showRationaleForContacts(request: PermissionRequest) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_contacts_rationale, request)
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    fun onCameraDenied() {
        Toast.makeText(this, "You dare deny the contacts", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    fun onCameraNeverAskAgain() {
        Toast.makeText(this, "You dare deny the contacts... FOREVER?", Toast.LENGTH_SHORT).show()
    }

    fun showRationaleDialog(@StringRes messageResId: Int, request: PermissionRequest) {
        AlertDialog.Builder(this)
                .setPositiveButton(R.string.button_allow) { _, _ -> request.proceed() }
                .setNegativeButton(R.string.button_deny) { _, _ -> request.cancel() }
                .setCancelable(false)
                .setMessage(messageResId)
                .show()
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
