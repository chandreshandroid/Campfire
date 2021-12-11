package com.mindhack.flymyowncustomer.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetDynamicStringDictionaryPojo1
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils


import com.google.gson.Gson

/**
 * Created by Chandresh on 08/12/2017.
 */

class PrefDb(internal var _context: Context) {


    // Shared Preferences
    internal var pref: SharedPreferences

    internal var editor: SharedPreferences.Editor

    // Shared pref mode
    internal var PRIVATE_MODE = 0

    val KEY_User_Type: String = "USERTYPE"
    val KEY_LATE : String = "LOCATIONLATE"
    val KEY_LONG : String = "LOCATIONLONG"

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun putInt(keyName: String, value: Int?) {
        editor.putInt(keyName, value!!)
        editor.commit()
    }

    fun putFloat(keyName: String, value: Float) {
        editor.putFloat(keyName, value)
        editor.commit()
    }

    fun saveCurrentLocation(late: String, long: String) {
        editor.putString(KEY_LATE, late)
        editor.putString(KEY_LONG, long)
        editor.commit()
    }

    fun getCurrentLocation() : String{
        pref.getString(KEY_LATE, "")
        pref.getString(KEY_LONG, "")
        return pref.getString(KEY_LATE, "") + ", " + pref.getString(KEY_LONG, "")
    }

    fun putString(keyName: String, value: String) {
        editor.putString(keyName, value)
        editor.commit()
    }

    fun putBoolean(keyName: String, value: String) {
        editor.putString(keyName, value)
        editor.commit()
    }

    fun PutInt(keyName: String, value: Int?) {
        editor.putInt(keyName, value!!)
        editor.commit()
    }

    fun clearValue(keyName: String) {
        editor.remove(keyName).commit()
    }

    fun clearPrefDb() {
        editor.clear()
        editor.commit()

    }

    public fun storeUserType(
        userType: String) {
        editor.putString(KEY_User_Type, userType)
        editor.commit()
        Log.d(TAG, "CouponCodeList login session modified!");
    }

    public fun getUserType() : String? {
        return pref.getString(KEY_User_Type, "")
    }

    public fun isMasterDataIn(): Boolean {

        if ((!getIsVerifyMaster()!!)) {
            editor.putBoolean(KEY_IS_MASTERIN, false)
            editor.putBoolean(KEY_IsVerifyMaster, false)
            editor.putString(KEY_MASTER_OBJ, "")
            editor.commit()
        }
        return pref.getBoolean(KEY_IS_MASTERIN, false)
    }

    public fun getIsVerifyMaster(): Boolean? {
        return pref.getBoolean(KEY_IsVerifyMaster, false)
    }

    /*fun getMasterData(): MasterPojo? {
        var gson = Gson()
        var json = pref.getString(KEY_MASTER_OBJ,"")
        return gson.fromJson<Any>(json, MasterPojo::class.java) as MasterPojo?
    }

    public fun set_Mastersession(Master: String) {
        editor.putBoolean(KEY_IS_MASTERIN, true)
        editor.putBoolean(KEY_IsVerifyMaster, true)
        editor.putString(KEY_MASTER_OBJ, Master)
        editor.commit()
        Log.d(SessionManager.TAG, "Master APi call modified!")
    }*/

    fun get_DynamicStringDictionary(): GetDynamicStringDictionaryPojo1? {

        val gson = Gson()
        val json = pref.getString(KEY_DynamicStringDictionary, "")
        return gson.fromJson<Any>(json, GetDynamicStringDictionaryPojo1::class.java) as GetDynamicStringDictionaryPojo1?
    }

    fun setDynamicStringDictionary(defaultSettings: String) {

        Log.d(TAG, "DynamicStringDictionary modified!")
        editor.putString(KEY_DynamicStringDictionary, defaultSettings)
        editor.commit()
    }

    fun getInt(keyName: String): Int? {
        return pref.getInt(keyName, 0)
    }

    fun getFloat(keyName: String): Float {
        return pref.getFloat(keyName, 0f)
    }

    fun getBoolean(keyName: String): Boolean {
        return pref.getBoolean(keyName, false)
    }

    fun getString(keyName: String): String? {
        return if (keyName.equals(MyUtils.SharedPreferencesenum.languageId.toString(), ignoreCase = true))
            pref.getString(keyName, "")
        else
            pref.getString(keyName, "")
    }

    companion object {

        // LogCat tag
        private val TAG = PrefDb::class.java.simpleName

        // Shared preferences file name
        private val PREF_NAME = "PrefDb"
        private val KEY_MASTER_OBJ: String = "MsterData"
        private val KEY_IS_MASTERIN: String = "isMasterData"
        private val KEY_IsVerifyMaster: String = "isVerifyMaster"
        private val KEY_DynamicStringDictionary: String = "DynamicStringDictionary"

    }

    fun saveFBAccessToken(token: String){

        editor.putString("fb_access_token", token)
        editor.apply()
    }

    fun getFBToken(): String?{

        return pref.getString("fb_access_token", null)
    }

    fun clearFBToken(){

        editor.clear()
        editor.apply()
    }


}
