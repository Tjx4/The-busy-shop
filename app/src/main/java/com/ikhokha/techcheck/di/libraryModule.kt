package com.ikhokha.techcheck.di

import com.google.firebase.FirebaseApp
import com.ikhokha.core.persistance.room.CartDB
import com.ikhokha.core.persistance.sharedPrefs.SharedPrefs
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import org.koin.dsl.koinApplication

val libraryModule = module {
    single { CartDB.getInstance(androidApplication()) }
    single { SharedPrefs.getInstance(androidApplication()) }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseStorage.getInstance() }
}