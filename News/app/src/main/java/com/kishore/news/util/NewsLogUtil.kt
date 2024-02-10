package com.kishore.news.util

import android.util.Log

object NewsLogUtil {
    private const val TAG = "NewsLogUtil"

    fun e(msg: String) {
        Log.e(TAG,msg)
    }

    fun i(msg: String) {
//        when (BuildConfig.BUILD_TYPE) {
//          "debug" -> {
//              Log.i(TAG,msg)
//          }
//        }
        Log.i(TAG,msg)
    }

    fun d(msg: String) {
        Log.d(TAG,msg)
    }

}