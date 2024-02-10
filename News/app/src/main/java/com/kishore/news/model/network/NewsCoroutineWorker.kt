package com.kishore.news.model.network

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kishore.news.util.NewsLogUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NewsCoroutineWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val networkDataSource: NewsNetworkDataSource
) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        NewsLogUtil.d("NewsCoroutineWorker do work")
        networkDataSource.getHeadlinesFromNetwork()
        networkDataSource.getAllNewsFromNetwork()
        return Result.success()
    }
}
