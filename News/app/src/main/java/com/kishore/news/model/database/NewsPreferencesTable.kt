package com.kishore.news.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "newspreference")
data class NewsPreferencesTable(
    @PrimaryKey @ColumnInfo(name = "keyname") val keyname: String,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "countrycode") val countryCode: String)
