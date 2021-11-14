package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.network.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    private val repo: NewsRepository,
    val app: Application
) : AndroidViewModel(app) {

    var breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingPageNumber = 1
    var searchPageNumber = 1
    var breakingNewsResponse: NewsResponse? = null
    var searchResponse: NewsResponse? = null

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        getBreakingNewsSafely(countryCode)
    }

    private suspend fun getBreakingNewsSafely(countryCode: String) {
        breakingNews.postValue(Resource.Loading())

        try {
            if (isInternetAvailable()) {
                val response = repo.getBreakingNews(countryCode, breakingPageNumber)
                breakingNews.postValue(handleBreakingResponse(response))
            } else {
                breakingNews.postValue(Resource.Error<NewsResponse>(message = "Error occurred, No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error(message = "Network Error"))
                else -> breakingNews.postValue(Resource.Error(message = "Conversion Error"))
            }
        }

    }

    fun searchNews(keyWord: String) = viewModelScope.launch {

        searchNewsSafely(keyWord)

    }

    private suspend fun searchNewsSafely(keyWord: String) {
        searchNews.postValue(Resource.Loading())

        try {
            if (isInternetAvailable()) {
                val response = repo.searchNews(keyWord, breakingPageNumber)
                searchNews.postValue(handleSearchResponse(response))
            } else {
                searchNews.postValue(Resource.Error(message = "Error occurred, No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error(message = "Network Error"))
                else -> searchNews.postValue(Resource.Error(message = "Conversion Error"))
            }
        }

    }

    private fun handleBreakingResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        breakingPageNumber++
        if (response.isSuccessful) {
            response.body()?.let { successfulResponse ->
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = successfulResponse
                } else {
                    val newArticles = successfulResponse.articles
                    breakingNewsResponse?.articles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: successfulResponse)
            }
        }

        return Resource.Error(response.body(), response.message())
    }

    private fun handleSearchResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        searchPageNumber++
        if (response.isSuccessful) {
            response.body()?.let { successfulResponse ->
                if (searchResponse == null) {
                    searchResponse = successfulResponse
                } else {
                    val newArticles = successfulResponse.articles
                    searchResponse?.articles?.addAll(newArticles)
                }
                return Resource.Success(searchResponse ?: successfulResponse)
            }
        }

        return Resource.Error(response.body(), response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        repo.saveArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repo.deleteArticle(article)
    }

    fun getSavedNews() =
        repo.getAllSavedNews()

    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getApplication<NewsApp>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }

            }

        }
        return false
    }
}