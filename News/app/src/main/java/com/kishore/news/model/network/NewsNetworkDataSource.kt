package com.kishore.news.model.network

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.kishore.news.model.database.NewsDatabaseWorker
import com.kishore.news.model.database.NewsTable
import com.kishore.news.model.network.newsapi.NewsRetrofitClient
import com.kishore.news.util.NewsLogUtil
import com.kishore.news.util.NewsUtil
import com.kishore.news.util.dependency.NewsSharedPreference
import com.kishore.news.util.dependency.NewsStringsDependency
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class NewsNetworkDataSource(context: Context) {


    private val TAG = NewsNetworkDataSource::class.java.simpleName
    private var mContext: Context

    private var  mHeadlines :MutableLiveData<List<NewsTable?>>
    private var  mAllNews :MutableLiveData<List<NewsTable?>>
    private var mySharedPreferences: NewsSharedPreference
    private var stringDependency : NewsStringsDependency

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NewsSharedInterface  {
        val newsSharedPreference : NewsSharedPreference
        val newsStringDependency : NewsStringsDependency
    }

    init {
        mContext = context
        mySharedPreferences = EntryPoints.get(mContext, NewsSharedInterface::class.java).newsSharedPreference
        stringDependency = EntryPoints.get(mContext, NewsSharedInterface::class.java).newsStringDependency
        mHeadlines = MutableLiveData<List<NewsTable?>>()
        mAllNews  = MutableLiveData<List<NewsTable?>>()
    }

/*    Commented as we using Hilt for dependency Injection
    companion object {
        // The number of days we want our API to return, set to 14 days or two weeks

        // For Singleton instantiation
        @Volatile
        private var instance: NewsNetworkDataSource? = null

        /**
         * Get the singleton for this class
         */
        fun getInstance(context: Context): NewsNetworkDataSource {
            return instance ?: synchronized(this) {
                instance ?: NewsNetworkDataSource(context).also { instance = it }
            }
        }
    }

 */

    fun getHeadlines(): LiveData<List<NewsTable?>> {
        return mHeadlines
    }


    fun getAllNews(): LiveData<List<NewsTable?>> {
        return mAllNews
    }

    /*
      Using co routine in Retrofit for getting news data from server
   */
    fun getHeadlinesFromNetwork() {

        CoroutineScope(Dispatchers.IO).launch {
            val request =  NewsRetrofitClient.getInstance().getHeadlines(setHeadlinesParameters())
            withContext(Dispatchers.IO) {
                try {
                    val response = request.await()
                    // Do something with the response
                    NewsLogUtil.i(TAG+ "Fetch headlines using Co routinue Retrofit Success")
                    mHeadlines.postValue(response.toNewsTableDataList(1))
                } catch (e: HttpException) {
                    NewsLogUtil.e(TAG+ " Coroutine Fetch headlines Http  Error"+e.message() + " "+e.code() + e.response())
                } catch (e: Throwable) {
                    NewsLogUtil.e(TAG+ "Coroutine Error")
                }
            }
        }
    }

    /*
      Using co routine in Retrofit for getting newsdata from server
   */
    fun getAllNewsFromNetwork() {
        CoroutineScope(Dispatchers.IO).launch {
            val request =  NewsRetrofitClient.getInstance().getAllNews(setAllNewsParameters())
            withContext(Dispatchers.IO) {
                try {
                   // val fullresponse = request.await()
                    val response = request.await()
                    // Do something with the response
                    NewsLogUtil.i(TAG+ "Fetch AllNewsFrom using Coroutinue Retrofit Success")
                    mAllNews.postValue(response.toNewsTableDataList(0))
                } catch (e: HttpException) {
                    NewsLogUtil.e(TAG+ "Coroutine AllNewsFrom Http  Error"+e.message() + " "+e.code() + e.response())
                } catch (e: Throwable) {
                    NewsLogUtil.e(TAG+ "Coroutine Error")
                }
            }
        }
    }

     private fun setHeadlinesParameters() : Map<String, String> {
         val countryCode = mySharedPreferences.getStringPreference(mContext.getString(com.kishore.news.R.string.country_key), stringDependency.countryCode)
        // val countryCode = "us"
         NewsLogUtil.d(TAG+ " queryy setHeadlinesParameters *** "+countryCode)
         val data= mutableMapOf<String, String>()
         data["country"] = countryCode
         return data

     }

    private fun setAllNewsParameters() : Map<String, String> {
        val query = mySharedPreferences.getStringPreference(mContext.getString(com.kishore.news.R.string.country_entry_key), stringDependency.country)
     //   val query = "us"
        NewsLogUtil.d(TAG+ " queryy setAllNewsParameters *** $query")
        val data = mutableMapOf<String, String>()
        data["q"] = URLEncoder.encode(query, "UTF-8")
        data["from"] = NewsUtil.getformatedToday()
        data["to"] = NewsUtil.getformatedToday()
        data["sortBy"] = "publishedAt"
        data["pageSize"] = "40"
        return data
    }


    fun scheduleRecurringNewsSync() {
        val constraints: Constraints = Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.CONNECTED)
        }.build()

        val request: PeriodicWorkRequest = PeriodicWorkRequestBuilder<NewsCoroutineWorker>(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                                           TimeUnit.MINUTES, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(mContext).enqueue(request)
    }


    fun fetchNews() {
        val request = OneTimeWorkRequestBuilder<NewsCoroutineWorker>().build()
        WorkManager.getInstance(mContext).enqueue(request)
    }

    /* for testing purpose only */
    fun prePopulatenews() {
        NewsLogUtil.d(TAG+ "prepopulatenews news")
        val request = OneTimeWorkRequestBuilder<NewsDatabaseWorker>().build()
        WorkManager.getInstance(mContext).enqueue(request)
    }

}