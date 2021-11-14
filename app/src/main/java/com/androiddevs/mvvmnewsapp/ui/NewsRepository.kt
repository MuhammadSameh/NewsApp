package com.androiddevs.mvvmnewsapp.ui

import com.androiddevs.mvvmnewsapp.database.ArticleDatabase
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.network.RetrofitInstance

class NewsRepository(
    val database: ArticleDatabase
) {
    suspend fun saveArticle(item: Article) = database.getDao().insertUpdate(item)
    suspend fun deleteArticle(item: Article) {database.getDao().deleteArticle(item)}
    fun getAllSavedNews()=database.getDao().getAllArticles()

    suspend fun getBreakingNews(countryCode: String, page:Int) = RetrofitInstance.api.getBreaking(countryCode, page)
    suspend fun searchNews(searchKey: String, page: Int) = RetrofitInstance.api.getAll(searchKey, page)



}