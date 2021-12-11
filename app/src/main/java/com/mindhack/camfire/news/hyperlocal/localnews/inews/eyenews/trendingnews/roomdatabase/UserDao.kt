package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.roomdatabase

import androidx.room.Delete
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
public interface UserDao {

    @get:Query("SELECT * FROM userlastsearch ORDER BY search_time")
    val all: List<User>

    @Query("SELECT * FROM userlastsearch where search_location_name LIKE  :firstName AND search_time LIKE :lastName")
    fun findByName(firstName: String, lastName: String): User

    @Query("SELECT COUNT(*) from userlastsearch")
    fun countUsers(): Int

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM userlastsearch WHERE searchid = :userId")
    fun deleteByUserId(userId: Int)

    @Query("DELETE FROM userlastsearch")
    fun deleteAllDatae()

    @Query("UPDATE userlastsearch SET search_time = :end_address WHERE searchid = :tid")
    fun updateSerachTime(tid: Long, end_address: String): Int

}