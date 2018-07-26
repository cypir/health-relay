package com.cypir.healthrelay


import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceManager
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takisoft.fix.support.v7.preference.EditTextPreference
import android.text.method.TextKeyListener.clear
import android.R.id.edit
import android.content.SharedPreferences



/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String) {
        val defaultInterval = resources.getString(R.string.interval_default)
        val intervalKey = resources.getString(R.string.interval_key)

        if (key == intervalKey) {
            val connectionPref = findPreference(key)
            // Set summary to be the user-description for the selected value
            connectionPref.summary = sharedPreferences?.getString(key, defaultInterval)
        }
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.fragment_preferences)

        val defaultInterval = resources.getString(R.string.interval_default)
        val intervalKey = resources.getString(R.string.interval_key)

        val editTextPreference = findPreference(intervalKey) as EditTextPreference
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        editTextPreference.summary = sharedPreferences.getString(intervalKey, defaultInterval)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }
}
