package com.kishore.news

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kishore.news.util.NewsUtil
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class NewsApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /*
        Needed has we are using Hilt in worker manager.
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build();
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setUserId(NewsUtil.getUniqueId(this))
        FirebaseCrashlytics.getInstance().setCustomKey("userId",NewsUtil.getUniqueId(this))

       /* if (applicationContext.packageName.equals("com.kishore.news")) {
            FirebaseCrashlytics.getInstance().setUserId(NewsUtil.getUniqueId(this))
            FirebaseCrashlytics.getInstance().setCustomKey("userId",NewsUtil.getUniqueId(this))
        } else {
            val state: Boolean = false
            FirebaseApp.getInstance().setDataCollectionDefaultEnabled(state)
        }*/
    }

}