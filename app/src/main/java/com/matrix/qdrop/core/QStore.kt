package com.matrix.qdrop.core

import android.content.Context
import android.content.SharedPreferences

class QStore(context: Context) {
    var sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.STORE_NAME, Context.MODE_PRIVATE)
    var editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun save(key: String, value: Any) {
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
        }
        editor.apply()
    }

    fun remove(key: String) {
        editor.remove(key)
        editor.apply()
    }

    fun get(key: String, defaultValue: Any): Any {
        when (defaultValue) {
            is String -> return sharedPreferences.getString(key, defaultValue) ?: defaultValue
            is Int -> return sharedPreferences.getInt(key, defaultValue)
            is Boolean -> return sharedPreferences.getBoolean(key, defaultValue)
        }
        return defaultValue
    }
}