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
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.fragment_preferences)

        val defaultInterval = resources.getString(R.string.interval_default) //TODO make this a constant in R
        val intervalKey = resources.getString(R.string.interval_key)

        val editTextPreference = findPreference(intervalKey) as EditTextPreference
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        editTextPreference.summary = sharedPreferences.getString(intervalKey, defaultInterval)

        //TODO make a listener that updates the summary on change
    }
}
