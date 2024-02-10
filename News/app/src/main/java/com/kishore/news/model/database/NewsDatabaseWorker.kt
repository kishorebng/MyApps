package com.kishore.news.model.database

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.kishore.news.model.network.newsapiresponse.NewsData
import com.kishore.news.util.NewsLogUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class NewsDatabaseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val database: NewsDataBase
) : CoroutineWorker(context, workerParams) {

    private val TAG by lazy { NewsDatabaseWorker::class.java.simpleName }
    val NEWS_JSON_FILENAME = "news.json"

    override suspend fun doWork(): Result = coroutineScope {
        NewsLogUtil.d("pre populate news doWork")
        try {
            applicationContext.assets.open(NEWS_JSON_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val data = object : TypeToken<NewsData>() {}.type
                    val news: NewsData = Gson().fromJson(jsonReader, data)
//                    database.newsDao().bulkInsert(*newsDetail.toNewsTableData())
                    database.newsDao().insertAll(news.toNewsTableDataList(1) as List<NewsTable>)
                    database.newsDao().insertAll(news.toNewsTableDataList(0) as List<NewsTable>)
                    Result.success()
                }
            }
        } catch (ex: Exception) {
            NewsLogUtil.e("prepopulatenews Error News database ${ex.message}")
            Result.failure()
        }
    }
}