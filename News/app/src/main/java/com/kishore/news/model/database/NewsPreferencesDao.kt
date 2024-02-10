package com.kishore.news.model.database

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsPreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(newspreference: NewsPreferencesTable)

    @Query("SELECT value FROM newspreference WHERE keyname =:keyname")
    fun getPreferenceValue(keyname: String): Cursor

    @Query("SELECT * FROM newspreference WHERE keyname =:keyname")
    fun getPreference(keyname: String): Cursor

    @Query("SELECT * FROM newspreference")
    fun getAll(): Cursor

}