package com.kishore.news.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kishore.news.R
import com.kishore.news.model.NewsRepository
import com.kishore.news.util.NewsFirebaseLogging
import com.kishore.news.util.dependency.NewsSharedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NewsSettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
    Preference.OnPreferenceClickListener {


    private val LOG_TAG = NewsSettingsFragment::class.java.simpleName

    @Inject
    lateinit var newsRepository: NewsRepository

    @Inject lateinit var mySharedPreferences: NewsSharedPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.news_settings, rootKey)

        (preferenceManager.findPreference(
            getString(R.string.country_key)
        ) as ListPreference?)?.onPreferenceChangeListener = this

        (preferenceManager.findPreference(
            getString(R.string.share_app_key)
        ) as Preference?)?.onPreferenceClickListener = this
        (preferenceManager.findPreference(
            getString(R.string.feedback_key)
        ) as Preference?)?.onPreferenceClickListener = this

        (requireActivity().packageManager.getApplicationInfo(requireActivity().packageName,0))

            (preferenceManager.findPreference(
            getString(R.string.version)
        ) as Preference?)?.summary = getVersionCode()

    }

    private fun getVersionCode() : String {
        val packageInfo = (requireActivity().packageManager.getPackageInfo(requireActivity().packageName,0))
        return packageInfo.versionName
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when (preference.key) {
            getString(R.string.country_key) -> {
                val listPreference = (preference as ListPreference)
                val index: Int =
                    listPreference.findIndexOfValue(newValue as String)
                val country = listPreference.entries[index]
                mySharedPreferences.putStringPreference(getString(R.string.country_entry_key),
                    country as String)
                newsRepository.fetchNews()
            }
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.share_app_key) -> {
                shareApp()
            }
            getString(R.string.feedback_key) -> {
                feedback()
            }
        }
        return true
    }

    private fun shareApp() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this News App! \n" + Uri.parse("https://play.google.com/store/apps/details?id=com.kishore.news"))
        startActivity(Intent.createChooser(shareIntent, "Share with"))
        NewsFirebaseLogging.logEvent("share_app",requireContext())
    }

    private fun feedback() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf("androkishore@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback about News App! \n")
        }
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
        NewsFirebaseLogging.logEvent("feedback",requireContext())
    }

}