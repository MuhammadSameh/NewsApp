package com.androiddevs.mvvmnewsapp.ui.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.adapters.ArticleAdapter
import com.androiddevs.mvvmnewsapp.network.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [SearchNewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchNewsFragment : Fragment() {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: ArticleAdapter
    val TAG = "SearchNews Fragment"
    var job: Job? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecycler()

        newsAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("Article",it)
            }

            findNavController().navigate(R.id.action_searchNewsFragment2_to_articleFragment, bundle)
        }

        etSearch.addTextChangedListener { editableText ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editableText?.let {
                    if (it.toString().isNotEmpty())
                        viewModel.searchNews(it.toString())
                }
            }

        }
        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Success ->{
                    hideProgressBar()
                    it.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                        val totalPages = it.totalResults/20 + 2
                        isLastPage = viewModel.searchPageNumber == totalPages
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let {
                        Log.e(TAG, it)
                        Toast.makeText(view.context,it,Toast.LENGTH_SHORT).show()
                    }

                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })


    }

    fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLastPage = false
    var isScrolling = false
    var isLoading = false

    val scrollingListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount


            val notLoadingAndNotLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + visibleItemCount >= layoutManager.itemCount
            val notFirstItem = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = layoutManager.itemCount >= 20

            val shouldPaginate = notLoadingAndNotLastPage && isLastItem && notFirstItem && isScrolling
                    && isTotalMoreThanVisible
            if (shouldPaginate){
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            }


        }
    }

    fun setupRecycler() {
        newsAdapter = ArticleAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollingListener)
        }
    }


}