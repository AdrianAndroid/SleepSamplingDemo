package com.sleepsampling

import android.content.Context
import android.content.SharedPreferences

object SPUtils {
    private val PREFERENCE_NAME = "S_P_S_L_G"

    private fun getSharedPreferences(): SharedPreferences {
        val context = SleepSamplingMonitor.getContext()
            ?: throw java.lang.NullPointerException("cannot be null!")
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun setString(key: String?, value: String?) {
        val sharedPreferences = getSharedPreferences()
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String?, defaultValue: String?): String? {
        val sharedPreferences = getSharedPreferences()
        return sharedPreferences.getString(key, defaultValue)
    }

    fun setBoolean(key: String?, value: Boolean) {
        val sharedPreferences = getSharedPreferences()
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        val sharedPreferences = getSharedPreferences()
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setInt(key: String?, value: Int) {
        val sharedPreferences = getSharedPreferences()
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        val sharedPreferences = getSharedPreferences()
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun setFloat(key: String?, value: Float) {
        val sharedPreferences = getSharedPreferences()
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getFloat(key: String?, defaultValue: Float): Float {
        val sharedPreferences = getSharedPreferences()
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun setLong(key: String?, value: Long) {
        val sharedPreferences = getSharedPreferences()
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String?, defaultValue: Long): Long {
        val sharedPreferences = getSharedPreferences()
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun clear(context: Context) {
        val sharedPreferences = getSharedPreferences()
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}