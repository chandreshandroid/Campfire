package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.roomdatabase

import android.os.AsyncTask
import android.util.Log
import androidx.annotation.NonNull


object DatabaseInitializer {

    private val TAG = DatabaseInitializer::class.java.name

    fun populateAsync(@NonNull db: AppDatabase) {
        val task = PopulateDbAsync(db)
        task.execute()
    }

    fun populateSync(@NonNull db: AppDatabase) {
        populateWithTestData(db)
    }

    private fun addUser(db: AppDatabase, user: User): User {
        db.userDao().insertAll(user)
        return user
    }

    private fun populateWithTestData(db: AppDatabase) {
        val user = User()
        user.searchLocationName = ""
        user.searchTime = ""
        addUser(db, user)

        val userList = db.userDao().all
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userList.size)
    }

    private class PopulateDbAsync internal constructor(private val mDb: AppDatabase) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            populateWithTestData(mDb)
            return null
        }
    }
}