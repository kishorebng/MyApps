package com.kishore.news.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object NewsFirebaseLogging {
    private const val TAG = "NewsFirebaseLogging"

    fun logEvent(event: String, context: Context) {
        NewsLogUtil.d(TAG + " user event $event")
        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setUserProperty("userId", NewsUtil.getAndroidId(context))
        firebaseAnalytics.setUserProperty("model", Build.MODEL)
        firebaseAnalytics.setUserProperty("oem", Build.MANUFACTURER)
        val bundle = Bundle()
        bundle.putString("action", event)
        firebaseAnalytics.logEvent("user_action", bundle)
    }

}