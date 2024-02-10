package com.kishore.news.viewmodel

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.kishore.news.R
import com.kishore.news.model.NewsRepository
import com.kishore.news.model.database.NewsTable
import com.kishore.news.util.NewsUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class NewsDetailsViewModel @AssistedInject constructor(newsRepository: NewsRepository,  @Assisted newsId: Int) : BaseObservable() {

    // It's a factory of this viewmodel, we need
    // to annotate this factory interface
    // with @AssistedFactory in order to
    // let Dagger-Hilt know about it
    @AssistedFactory
    interface NewsDetailsViewModelFactory {
        fun create(newsId: Int): NewsDetailsViewModel
    }


    @Bindable
    var newsDetail: NewsTable? = newsRepository.getNewsDetail(newsId)

    /*
       @JvmStatic is just work around for Bindingadapter static method fix
    */
    companion object {

        @BindingAdapter("android:src")
        @JvmStatic
        fun loadImage(view: ImageView, imageUrl: String?) {
            view.setImageResource(R.drawable.my_news)
            Glide.with(view.context)
                    .load(imageUrl)
                    .error(R.drawable.my_news)
                    .into(view)
        }

        @BindingAdapter("localeDate")
        @JvmStatic
        fun localeDate(view: TextView, publishedAt: String?) {
            view.setText(NewsUtil.formatDate(publishedAt))
        }

        @BindingAdapter("content", "description")
        @JvmStatic
        fun content(view: TextView, contentdata: String?, description: String) {
            if (contentdata == null) {
                view.setText(description)
            } else {
                val index = contentdata.indexOf("[+")
                if ((index != -1)) {
                    view.setText(contentdata.substring(0, index))
                } else {
                    view.setText(contentdata)
                }
            }
        }
    }

}