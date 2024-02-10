package com.kishore.news.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.kishore.news.model.NewsRepository
import com.kishore.news.model.database.NewsTable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(val newsRepository: NewsRepository) : ViewModel() {

      var newsdata: LiveData<List<NewsTable>>
      var headlinesnewsdata: LiveData<List<NewsTable>> = newsRepository.getHeadlines()

      private val query = MutableLiveData<String>()
      init {
         /* newsdata =  Transformations.switchMap(
                  query,
                  ::allNews
          )*/

          newsdata = query.switchMap { search -> newsRepository.getSearchResult(search) }

          query.value = "%%"
      }

      fun searchNews(queryString: String) = apply { query.value = queryString  }

     fun allNews(query: String) =  newsRepository.getSearchResult(query)

     fun fetchNews() {
         newsRepository.fetchNews()
     }


}