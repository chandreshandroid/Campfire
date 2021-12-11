package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.roomdatabase

import androidx.room.RoomDatabase
import androidx.room.Database


@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}