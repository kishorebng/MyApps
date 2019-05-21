package com.kishore.news.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.kishore.news.InjectorUtils
import com.kishore.news.databinding.FragmentNewsdetailsBinding
import com.kishore.news.viewmodel.NewsDetailsViewModel
import android.content.Intent
import android.net.Uri
import com.kishore.news.R


class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)

    }

}




