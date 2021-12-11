package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


/**
 * Created by ADMIN on 31/03/2018.
 */

class SettingsManager(internal var _context: Context) {
    internal var PRIVATE_MODE = 0
    internal var pref: SharedPreferences
    internal var editor: SharedPreferences.Editor

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun saveDefaultSettings(defaultSettings: String) {
        editor.putString(KEY_USER_SETTINGS, defaultSettings)
        editor.commit()
        Log.d(TAG, "User settings modified!")
    }

    /*public UserSettingsJsonPojo get_DefaultSettings() {

        Gson gson = new Gson();
        String json = pref.getString(KEY_USER_SETTINGS, "");
        UserSettingsJsonPojo settings = gson.fromJson(json, UserSettingsJsonPojo.class);
        return settings;
    }*/

    fun clear_settings() {
        editor.clear()
        editor.commit()
    }

    companion object {

        private val KEY_USER_SETTINGS = "DefaultSettings"
        private val TAG = SessionManager::class.java.simpleName
        private val PREF_NAME = "Settings"
    }
}
