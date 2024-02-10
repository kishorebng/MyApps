package com.kishore.news.view


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.kishore.news.R
import com.kishore.news.databinding.FragmentNewslistBinding
import com.kishore.news.util.NewsFirebaseLogging
import com.kishore.news.util.NewsLogUtil
import com.kishore.news.util.dependency.Country
import com.kishore.news.util.dependency.CountryCode
import com.kishore.news.util.dependency.NewsSharedPreference
import com.kishore.news.viewmodel.NewsListViewModel
import com.kishore.news.viewmodel.NewsListViewModelFactory
import com.kishore.news.viewmodel.NewsListViewModelOld
import com.kishore.news.viewmodel.adapter.NewsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NewsListFragment : Fragment(), NewsListAdapter.NewsListOnItemClickHandler, SearchView.OnQueryTextListener {

    lateinit var mContext: Context
    private lateinit var mNewsListBinding: FragmentNewslistBinding
//    private lateinit var mNewsListViewModel: NewsListViewModel
    private lateinit var mheadlinesListAdapter: NewsListAdapter
    private lateinit var mNewsListAdapter: NewsListAdapter
    private lateinit var headlineslayoutManager : LinearLayoutManager


    @Inject lateinit var mySharedPreferencesHilt: NewsSharedPreference
    @Inject lateinit var factory: NewsListViewModelFactory

//    val mNewsListViewModel: NewsListViewModelOld by viewModels {
//        factory
//    }

    private val mNewsListViewModel: NewsListViewModel by viewModels()

  //  private val viewModel by viewModels<NewsListViewModel>()


    @CountryCode
    @Inject lateinit var countryCode: String

    @Country
    @Inject lateinit var country: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mNewsListBinding = FragmentNewslistBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return mNewsListBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @SuppressLint("WrongConstant")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

 //     InjectorUtils.getNewsRepository(requireActivity()).prepopulatenews(mContext)

        headlineslayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mNewsListBinding.headlinesList!!.layoutManager = headlineslayoutManager

        mheadlinesListAdapter = NewsListAdapter(requireActivity(),true, this)
        mNewsListBinding.headlinesList.adapter = mheadlinesListAdapter

        val helper = PagerSnapHelper()
        helper.attachToRecyclerView(mNewsListBinding.headlinesList)

        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mNewsListBinding.newsList!!.layoutManager = layoutManager

        mNewsListAdapter = NewsListAdapter(requireActivity(), false,this)
        mNewsListBinding.newsList.adapter = mNewsListAdapter

//        val factory : NewsListViewModelFactory = InjectorUtils.provideNewsListViewModelFactory(requireActivity().applicationContext)
      //  mNewsListViewModel = ViewModelProviders.of(this, factory).get(NewsListViewModel::class.java)

        mNewsListViewModel!!.newsdata.observe(this, Observer { newsEntries  ->
            this.mNewsListAdapter!!.updateData(newsEntries!!)
            if (this.mNewsListAdapter!!.itemCount == 0) {
                mNewsListBinding.newsListEmpty.visibility = View.VISIBLE
            } else {
                mNewsListBinding.newsListEmpty.visibility = View.GONE
            }
        })

        mNewsListViewModel!!.headlinesnewsdata.observe(this, Observer { newsEntries  ->
            this.mheadlinesListAdapter.updateData(newsEntries!!)
        })

//        mNewsListViewModel!!.headlinesnewsdata.observeForever { newsEntries ->
//            this.mheadlinesListAdapter!!.updateData(newsEntries!!)
//        }
//
//        mNewsListViewModel!!.newsdata.observeForever { newsEntries ->
//
//            this.mNewsListAdapter!!.updateData(newsEntries!!)
//        }

        val shared= mySharedPreferencesHilt.getStringPreference(getString(com.kishore.news.R.string.country_entry_key),"")
        if(TextUtils.isEmpty(shared)) {
            mySharedPreferencesHilt.putStringPreference(getString(com.kishore.news.R.string.country_key),
                countryCode)
            mySharedPreferencesHilt.putStringPreference(getString(com.kishore.news.R.string.country_entry_key),
                country)
        }

        autoScroll()
    }


      fun autoScroll() {
      //  val firstCompletelyItemVisible = headlineslayoutManager.findFirstCompletelyVisibleItemPosition()
        val speedScroll : Long = 3500
        val handler = Handler(Looper.getMainLooper())
          val runnable = object : Runnable {
            var visibleItem = 0
            override fun run() {
                if (isVisible && getUserVisibleHint()) {
                    visibleItem =   headlineslayoutManager.findFirstVisibleItemPosition()
                    if (visibleItem == mheadlinesListAdapter.getItemCount()-1) {
                        visibleItem = 0
                        mNewsListBinding.headlinesList.scrollToPosition(0)
                    }
                    if (visibleItem < mheadlinesListAdapter.getItemCount()) {
                        mNewsListBinding.headlinesList.smoothScrollToPosition(++visibleItem)
                    }
                    NewsLogUtil.d("count "+ visibleItem)
                }
                handler.postDelayed(this, speedScroll)
            }
        }
        handler.postDelayed(runnable, speedScroll)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.newslist_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView : SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item!!.itemId) {
            R.id.action_settings -> {
                findNavController().navigate(NewsListFragmentDirections.actionNewsSettings())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val queryText = "*$query*"
        mNewsListViewModel.searchNews(queryText)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val queryText = "*$newText*"
        mNewsListViewModel.searchNews("%"+newText+"%")
        return true
    }

    override fun onItemClick(url : String?) {
        url?.let {
            val viewIntent = Intent()
            viewIntent.action = Intent.ACTION_VIEW
            viewIntent.setData(Uri.parse(it))
            startActivity(viewIntent)
            NewsFirebaseLogging.logEvent("view_news", requireContext())
        }
    }

    override fun onShareClick(url: String?) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this News! \n" + Uri.parse(url))
        startActivity(Intent.createChooser(shareIntent, "Share with"))
        NewsFirebaseLogging.logEvent("share_news", requireContext())
    }

}
