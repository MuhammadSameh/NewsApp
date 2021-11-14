package com.androiddevs.mvvmnewsapp.network

import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.network.Utils.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET ("v2/top-headlines")
    suspend fun getBreaking (
        @Query ("country")
        country: String = "eg",
        @Query("page")
        page: Int,
        @Query ("apiKey")
        key: String = API_KEY
    ) : Response<NewsResponse>


    @GET ("v2/everything")
    suspend fun getAll (
        @Query ("q")
        searchKey: String,
        @Query("page")
        page: Int,
        @Query ("apiKey")
        key: String = API_KEY
    ) : Response<NewsResponse>


}