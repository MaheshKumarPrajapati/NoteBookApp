package com.maheshprajapati.myapplication.utility

import android.content.Context

class HelperClass(private val context: Context) {

    var IS_APP_LOCK_ACTIVE: String = "IS_APP_LOCK_ACTIVE"
    var LOCK_PAUSE_TIME: String = "LOCK_PAUSE_TIME"
    var MINUTES_TO_LOCK: String = "MINUTES_TO_LOCK"
    private val APP_MAIN_PREF = "app_pref"


    fun savePreferences(key: String?, value: String?) {
        val preferences = context.getSharedPreferences(
            APP_MAIN_PREF, Context.MODE_PRIVATE
        )
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.commit()
    }

    /**
     * Method To Load Preferences
     *
     * @param key The string value of the preference to load
     * @returns value The string value of the key passed
     */
    fun loadPreferences(key: String?): String {
        var strValue: String = ""
        val preferences = context.getSharedPreferences(APP_MAIN_PREF, Context.MODE_PRIVATE)
        strValue = preferences.getString(key, "").toString()
        return strValue
    }

    fun saveBoolPreferences(key: String?, value: Boolean) {
        val preferences = context.getSharedPreferences(
            APP_MAIN_PREF, Context.MODE_PRIVATE
        )
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun clearPreferences() {
        val preferences = context.getSharedPreferences(
            APP_MAIN_PREF, Context.MODE_PRIVATE
        )
        val editor = preferences.edit()
        editor.clear()
        editor.commit()
    }

    fun loadBoolPreferences(key: String?): Boolean {
        var value = false
        val preferences = context.getSharedPreferences(
            APP_MAIN_PREF, Context.MODE_PRIVATE
        )
        value = preferences.getBoolean(key, false)
        return value
    }
}