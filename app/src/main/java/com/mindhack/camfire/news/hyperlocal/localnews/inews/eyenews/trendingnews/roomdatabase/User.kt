package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "userlastsearch")
class User {

    @PrimaryKey(autoGenerate = true)
    var searchid: Int = 0

    @ColumnInfo(name = "search_location_name")
    var searchLocationName: String? = null

    @ColumnInfo(name = "search_location_late")
    var searchLocationLate: Double? = null

    @ColumnInfo(name = "search_location_long")
    var searchLocationLong: Double? = null

    @ColumnInfo(name = "search_time")
    var searchTime: String? = null

    @ColumnInfo(name = "search_video")
    var searchVideo: Boolean? = true
}