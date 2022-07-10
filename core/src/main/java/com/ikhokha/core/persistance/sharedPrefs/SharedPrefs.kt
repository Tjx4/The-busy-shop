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