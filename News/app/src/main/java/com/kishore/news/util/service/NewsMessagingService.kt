package com.kishore.news.util.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kishore.news.model.NewsRepository
import com.kishore.news.model.database.NewsDataBase
import com.kishore.news.model.database.NewsPreferencesTable
import com.kishore.news.util.NewsLogUtil
import com.kishore.news.util.dependency.CountryCode
import com.kishore.news.util.dependency.NewsSharedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewsMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var newsRepository : NewsRepository

    @Inject
    lateinit var newsSharedPreference : NewsSharedPreference

    @CountryCode
    @Inject lateinit var defaultCountryCode: String

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        NewsLogUtil.i("onMessageReceived Kishore  ${message.messageType!!}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val countryCode = newsSharedPreference.getStringPreference(getString(com.kishore.news.R.string.country_key), defaultCountryCode)
        NewsLogUtil.i("onNewToken $token  and country code is $countryCode")
            newsRepository?.let {
                it.insertToken(
                    NewsPreferencesTable(
                        "App_Token",
                       token,countryCode)
                )
            }
    }

}