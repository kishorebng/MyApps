package com.kishore.news.model.database

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kishore.news.model.database.utility.DateConverter
import com.kishore.news.model.network.NewsNetworkDataSource
import com.kishore.news.util.NewsLogUtil
import dagger.hilt.EntryPoints

@Database (entities = [NewsTable::class,NewsPreferencesTable::class],
    version = 3)
@TypeConverters(DateConverter::class)
abstract class NewsDataBase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    abstract fun newsPreferencesDao(): NewsPreferencesDao

    companion object {

//        // For Singleton instantiation
//        @Volatile private var instance: NewsDataBase? = null
//
//        fun getInstance(context: Context): NewsDataBase {
//            return instance ?: synchronized(this) {
//                instance ?: buildDatabase(context).also { instance = it }
//            }
//        }

        fun buildDatabase(context: Context): NewsDataBase {
            return Room.databaseBuilder(context, NewsDataBase::class.java, "newsDetail-db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            NewsLogUtil.i("Database created ")
                        }
                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            NewsLogUtil.i("onDestructive called ")
                        }

                    })
                    .build()
        }
    }

}
