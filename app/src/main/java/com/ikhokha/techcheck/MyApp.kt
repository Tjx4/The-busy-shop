package com.ikhokha.techcheck

import android.app.Application
import com.ikhokha.techcheck.di.repositoryModule
import com.ikhokha.techcheck.di.viewModelModule
import com.ikhokha.techcheck.di.ModuleLoadHelper
import com.ikhokha.techcheck.di.libraryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(
                listOf(
                    viewModelModule,
                    repositoryModule,
                    libraryModule
                ) + ModuleLoadHelper.getBuildSpecialModuleList()
            )
        }
    }
}