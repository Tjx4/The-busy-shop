package com.ikhokha.features.cart.core.sharedPrefs

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.ikhokha.common.constants.PREFERENCE

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