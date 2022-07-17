package com.ikhokha.core.persistance.sharedPrefs

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ikhokha.common.constants.PREFERENCE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SharedPrefs(application: Application) {
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(
        "${application.packageName}$PREFERENCE",
        Context.MODE_PRIVATE
    )

    private val APP_SETUP = "app_setup"

    var isAppSetup: Boolean
        get() {
            val json = sharedPreferences.getString(APP_SETUP, "")
            return Gson().fromJson(json, Boolean::class.java) ?: false
        }
        set(isFirstTime) {
            val editor = sharedPreferences.edit()
            val connectionsJSONString = Gson().toJson(isFirstTime)
            editor.putString(APP_SETUP, connectionsJSONString)
            editor.commit()
        }

    companion object {
        fun getInstance(application: Application): SharedPrefs {
            synchronized(this) {
                var instance: SharedPrefs? = null

                if (instance == null) {
                    instance = SharedPrefs(application)
                }

                return instance
            }
        }
    }
}