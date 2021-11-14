package com.androiddevs.mvvmnewsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.models.Article

@Database (
    entities = [Article::class],
    version = 2
        )
@TypeConverters (Converter::class)
abstract class ArticleDatabase: RoomDatabase() {
    abstract fun getDao(): ArticleDao

    companion object{

        @Volatile
        private var mInstance: ArticleDatabase? = null

        private val lock = Any()

        operator fun invoke (context: Context) = mInstance ?: synchronized(lock){
            createDatabase(context).also {
                mInstance = it
            }
        }





        fun createDatabase(context: Context) = Room.databaseBuilder(context.applicationContext
            ,ArticleDatabase::class.java,"Articles.db").fallbackToDestructiveMigration().build()
    }
}