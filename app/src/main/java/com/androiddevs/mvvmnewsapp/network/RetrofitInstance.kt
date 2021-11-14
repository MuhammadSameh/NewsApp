package com.androiddevs.mvvmnewsapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        Retrofit.Builder().
        baseUrl("https://newsapi.org/").
        client(client).
        addConverterFactory(GsonConverterFactory.create())
            .build()

    }

     val api by lazy {
        retrofit.create(Api::class.java)
    }
}