package com.ikhokha.features.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ikhokha.common.constants.MAIN_ACTIVITY
import com.ikhokha.common.extensions.FADE_IN_ACTIVITY
import com.ikhokha.common.extensions.navigateToActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigateToActivity(MAIN_ACTIVITY, null, FADE_IN_ACTIVITY)
        finish()
    }
}