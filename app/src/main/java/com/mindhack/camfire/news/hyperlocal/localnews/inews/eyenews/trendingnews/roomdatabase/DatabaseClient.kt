package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.roomdatabase

import android.content.Context
import androidx.room.Room

class DatabaseClient private constructor(private val mCtx: Context) {
    //our app database object
    var appDatabase: AppDatabase?=null

    companion object {
        private var mInstance: DatabaseClient? = null
        @Synchronized
        fun getInstance(mCtx: Context): DatabaseClient? {
            if (mInstance == null) {
                mInstance = DatabaseClient(mCtx)
            }
            return mInstance
        }
    }

    init {
      appDatabase =
            Room.databaseBuilder(mCtx, AppDatabase::class.java, "user-database")
                .build()
    }
}