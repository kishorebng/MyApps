package com.kishore.news.model

import androidx.lifecycle.LiveData
import com.kishore.news.model.database.NewsDao
import com.kishore.news.model.database.NewsDataBase
import com.kishore.news.model.database.NewsPreferencesTable
import com.kishore.news.model.database.NewsTable
import com.kishore.news.model.network.NewsNetworkDataSource
import com.kishore.news.util.NewsLogUtil

class NewsRepository(newsDataBase: NewsDataBase,
                     newsNetworkDataSource: NewsNetworkDataSource) {

    private val LOG_TAG = NewsRepository::class.java.getSimpleName()
    private var mNewsDao: NewsDao

    private var mNewsDataBase: NewsDataBase
    private var mNewsNetworkDataSource: NewsNetworkDataSource

    private var mInitialized = false

    /*
      Commented as we using Hilt for dependency Injection
    companion object {
        // For Singleton instantiation
        private var instance: NewsRepository? = null

        fun getInstance(newsDataBase: NewsDataBase,
                        newsNetworkDataSource: NewsNetworkDataSource
        ) =
                instance ?: synchronized(this) {
                    instance
                            ?: NewsRepository(newsDataBase, newsNetworkDataSource).also { instance = it }
                }

    }

    /* this can be used if you want no argument in constructor */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NewsRepositotyInterface  {
        val newsDataBase : NewsDataBase
        val newsNetworkDataSource : NewsNetworkDataSource
    }
      mNewsDataBase = EntryPoints.get(applicationContext, NewsDatabaseInterface::class.java).newsDataBase
      mNewsNetworkDataSource = EntryPoints.get(applicationContext, NewsDatabaseInterface::class.java).newsNetworkDataSource
     */


    init {
        mNewsDataBase = newsDataBase
        mNewsNetworkDataSource = newsNetworkDataSource
        // get dao objects
        mNewsDao = mNewsDataBase.newsDao()

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        val headlines = mNewsNetworkDataSource.getHeadlines()
        headlines.observeForever { breakingNewsFromNetwork ->
            NewsLogUtil.d(LOG_TAG+ "headlines changes")
            mNewsDao.deleteNews(NewsModelUtil.HEADLINE)
            mNewsDao.insertAll(breakingNewsFromNetwork as List<NewsTable>)
        }

        val allNews = mNewsNetworkDataSource.getAllNews()
        allNews.observeForever { allNewsFromNetwork ->
            NewsLogUtil.d(LOG_TAG+ "all news changes")
            mNewsDao.deleteNews(NewsModelUtil.NORMAL_NEWS)
            mNewsDao.insertAll(allNewsFromNetwork as List<NewsTable>)
        }
    }

    @Synchronized
    private fun initializeData() {
        NewsLogUtil.d("initializeData ")
        if (mInitialized) return
        mInitialized = true
        startRecurringService()
        fetchNews()
//        prePopulatenews()
    }


    fun getHeadlines(): LiveData<List<NewsTable>> {
        NewsLogUtil.d("getHeadlines ")
        initializeData()
        return mNewsDao.getheadlines()
    }

    fun getAllnews(): LiveData<List<NewsTable>>  {
//        val today = NewsModelUtil.getNormalizedUtcDateForToday()
        initializeData()
        return mNewsDao.getAllNews()
    }

    fun getNewsDetail(id: Int): NewsTable {
        return mNewsDao.getNewsDetail(id)
    }


    fun getSearchResult(query: String): LiveData<List<NewsTable>> {
        val searchResult: LiveData<List<NewsTable>> = mNewsDao.searchNews(query)
        return searchResult
    }


    fun prePopulatenews() {
//        val request = OneTimeWorkRequestBuilder<NewsDatabaseWorker>().build()
//        WorkManager.getInstance(context).enqueue(request)
        mNewsNetworkDataSource.prePopulatenews()
    }


    fun startRecurringService() {
        NewsLogUtil.d("startRecurringService ")
        mNewsNetworkDataSource.scheduleRecurringNewsSync()
    }


    fun fetchNews() {
        mNewsNetworkDataSource.fetchNews()
    }

    fun testinsertToken() {

        mNewsDataBase.newsPreferencesDao()
            .insertOrUpdate(
                NewsPreferencesTable(
                    "App_Token",
                    "1122","us")
            )
    }

    fun insertToken(newsPreferencesTable : NewsPreferencesTable) {
        mNewsDataBase.newsPreferencesDao()
            .insertOrUpdate(
                newsPreferencesTable
            )
    }

}