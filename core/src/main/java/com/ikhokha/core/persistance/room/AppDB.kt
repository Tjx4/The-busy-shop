package com.ikhokha.core.persistance.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ikhokha.common.constants.APP_DB
import com.ikhokha.core.persistance.room.tables.users.UsersDAO
import com.ikhokha.core.persistance.room.tables.users.UsersTable

@Database(entities = [UsersTable::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract val usersDAO: UsersDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null

        fun getInstance(context: Context): AppDB {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDB::class.java,
                        APP_DB
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}