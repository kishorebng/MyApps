package com.kishore.news.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures.addCallback
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class NewsUtil {

    companion object {

        fun formatDate(input: String?): String {
            val input_DateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val output_DateFormat = "E MMM dd YYYY hh:mm a"
            val inputDateFormat = SimpleDateFormat(input_DateFormat)
            inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
            val date = inputDateFormat.parse(input)
            val outputDateFormat = SimpleDateFormat(output_DateFormat)
            return outputDateFormat.format(date)
        }

        fun getformatedToday(): String {
            val output_DateFormat = "yyyy-MM-dd"
            val outputDateFormat = SimpleDateFormat(output_DateFormat)
            return output_DateFormat.format(Date())
        }

//        fun getCountryCode () : String {
//            return Locale.getDefault().country.lowercase()
//        }
//
//        fun getDisplayCountry () : String {
//            return Locale.getDefault().displayCountry.lowercase()
//        }


        fun getAndroidId(context: Context): String {
            val androidId =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
            return androidId
        }

        fun getAdsId(context: Context): String {
            val advertisingIdInfo =
                AdvertisingIdClient.getAdvertisingIdInfo(context).get()
            return advertisingIdInfo.id
        }

        fun getUniqueId(context: Context): String {
            if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(context)) {
                return getAdsId(context)
            } else {
                return getAndroidId(context)
            }
        }

        private fun determineAdvertisingInfo(context: Context) {
            if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(context)) {
                val advertisingIdInfoListenableFuture =
                    AdvertisingIdClient.getAdvertisingIdInfo(context)

                addCallback(advertisingIdInfoListenableFuture,
                    object : FutureCallback<AdvertisingIdInfo> {
                        override fun onSuccess(adInfo: AdvertisingIdInfo?) {
                            val id: String? = adInfo?.id
                            val providerPackageName: String? = adInfo?.providerPackageName
                            val isLimitTrackingEnabled =
                                adInfo?.isLimitAdTrackingEnabled
                        }

                        override fun onFailure(t: Throwable) {
                            NewsLogUtil.e(
                                "Failed to connect to Advertising ID provider."
                            )
                            // Try to connect to the Advertising ID provider again, or fall
                            // back to an ads solution that doesn't require using the
                            // Advertising ID library.
                        }
                    }, Executors.newSingleThreadExecutor()
                )
            } else {
                // The Advertising ID client library is unavailable. Use a different
                // library to perform any required ads use cases.
            }
        }


        fun onShareNews(context: Context, url: String?) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this News! \n" + Uri.parse(url))
            context.startActivity(Intent.createChooser(shareIntent, "Share with"))
            NewsFirebaseLogging.logEvent("share_news", context)
        }


        fun shareApp(context: Context) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this News App! \n" + Uri.parse("https://play.google.com/store/apps/details?id=com.kishore.news")
            )
            context.startActivity(Intent.createChooser(shareIntent, "Share with"))
            NewsFirebaseLogging.logEvent("share_app", context)
        }

        fun feedback(context: Context) {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf("androkishore@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Feedback about News App! \n")
            }
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
            NewsFirebaseLogging.logEvent("feedback", context)
        }

        fun viewSite(context: Context, url: String) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_VIEW
            shareIntent.data = Uri.parse(url)
            context.startActivity(shareIntent)
            NewsFirebaseLogging.logEvent("view_link", context)
        }

        fun viewNews(url: String?, context: Context) {
            url?.let {
                val viewIntent = Intent()
                viewIntent.action = Intent.ACTION_VIEW
                viewIntent.setData(Uri.parse(it))
                context.startActivity(viewIntent)
                NewsFirebaseLogging.logEvent("view_news", context)
            }
        }

        fun getVersionCode(context: Context): String {
            val packageInfo = (context.packageManager.getPackageInfo(context.packageName, 0))
            return packageInfo.versionName
        }

    }
}