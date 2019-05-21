package com.kishore.news.view

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.kishore.news.InjectorUtils
import com.kishore.news.R


class NewsSettingsFragment : PreferenceFragmentCompat() , OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.news_settings, rootKey)

        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val preference = preferenceManager.findPreference(
                getString(R.string.about_key))

//        preference
//                .setOnPreferenceClickListener(object : OnPreferenceClickListener() {
//
//                    fun onPreferenceClick(preference: Preference): Boolean {
//                        showAbout()
//                        return true
//                    }
//                })
        preference
                .setOnPreferenceClickListener(this)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
        when (key) {
            getString(R.string.country_key) -> {
                val preference = findPreference(key) as ListPreference
                sharedPreferences.putString(getString(com.kishore.news.R.string.country_entry_key),preference.entry.toString())
                sharedPreferences.apply()
                InjectorUtils.getNewsRepository(requireActivity()).fetchNews()
            }
        }
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val key = preference!!.key
        when (key) {
            getString(R.string.about_key) -> {
               findNavController().navigate(NewsSettingsFragmentDirections.actionAbout())
            }
        }
        return true
    }


    fun launchAbout() {

    }

}