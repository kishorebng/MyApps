package com.kishore.news.view

import android.os.Bundle
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kishore.news.util.dependency.Country
import com.kishore.news.util.dependency.CountryCode
import com.kishore.news.util.dependency.NewsSharedPreference
import com.kishore.news.view.theme.NewsTheme
import com.kishore.news.viewmodel.NewsListViewModelOld
import com.kishore.news.viewmodel.NewsListViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject lateinit var mySharedPreferencesHilt: NewsSharedPreference

    @CountryCode
    @Inject lateinit var countryCode: String

    @Country
    @Inject lateinit var country: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val newsListViewModel: NewsListViewModelOld by viewModels {
//            newsListViewModelFactory
//        }

        val shared= mySharedPreferencesHilt.getStringPreference(getString(com.kishore.news.R.string.country_entry_key),"")
        if(TextUtils.isEmpty(shared)) {
            mySharedPreferencesHilt.putStringPreference(getString(com.kishore.news.R.string.country_key),
                countryCode)
            mySharedPreferencesHilt.putStringPreference(getString(com.kishore.news.R.string.country_entry_key),
                country)
        }

        setContent {
            NewsTheme {
                val newsNavController = rememberNavController()
                //NavGraph
                NavHost(navController = newsNavController, startDestination = "newsList") {
                    composable("newsList") { NewsListScreen(newsNavController) }
                    composable("preferences") { NewsPreferenceScreen(newsNavController,mySharedPreferencesHilt) }
                    /*...*/
                }
            }
        }
    }
}

