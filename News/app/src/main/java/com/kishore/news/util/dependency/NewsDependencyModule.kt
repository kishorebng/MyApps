package com.kishore.news.util.dependency

import android.content.Context
import androidx.preference.PreferenceManager
import com.kishore.news.model.NewsRepository
import com.kishore.news.model.database.NewsDataBase
import com.kishore.news.model.network.NewsNetworkDataSource
import com.kishore.news.viewmodel.NewsListViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class CountryCode

@Qualifier
annotation class Country

/*
  Module means the class which contains methods who will provide dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NewsDependencyModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): NewsSharedPreference {
        return NewsSharedPreference(PreferenceManager.getDefaultSharedPreferences(context))
    }

//    @Provides
//    @Singleton
//    fun provideNetworkDataSource(@ApplicationContext context: Context): NewsNetworkDataSource {
//        return NewsNetworkDataSource.getInstance(context.getApplicationContext())
//    }

    @Provides
    @Singleton
    fun provideNetworkDataSource(@ApplicationContext context: Context): NewsNetworkDataSource {
        return NewsNetworkDataSource(context.getApplicationContext())
    }

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): NewsDataBase {
        return NewsDataBase.buildDatabase(context.getApplicationContext())
    }

    @Provides
    @Singleton
    fun getNewsRepository(
        newsDataBase: NewsDataBase,
        newsNetworkDataSource: NewsNetworkDataSource
    ): NewsRepository {
        return NewsRepository(
            newsDataBase, newsNetworkDataSource
        )
    }

    @Provides
    @Singleton
    fun provideNewsListViewModelFactory(newsRepository : NewsRepository): NewsListViewModelFactory {
        return NewsListViewModelFactory(newsRepository)
    }

    // use assisted inject for this as id changes @ runtime
//    @Provides
//    fun provideNewsDetailsModel(newsRepository : NewsRepository, id :Int): NewsDetailsViewModel {
//        return NewsDetailsViewModel(newsRepository,id)
//    }

    @CountryCode
    @Provides
    fun providegetCountryCode () : String {
        return Locale.getDefault().country.lowercase()
    }

    @Country
    @Provides
    fun provideDisplayCountry () : String {
        return Locale.getDefault().displayCountry.lowercase()
    }
}

