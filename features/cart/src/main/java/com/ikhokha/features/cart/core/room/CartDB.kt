package com.ikhokha.features.cart.core.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ikhokha.common.constants.CART_DB
import com.ikhokha.features.cart.core.room.items.ItemsDAO
import com.ikhokha.features.cart.core.room.items.ItemsTable

@Database(entities = [ItemsTable::class], version = 1, exportSchema = false)
abstract class CartDB : RoomDatabase() {
    abstract val itemsDAO: ItemsDAO

    companion object {
        @Volatile
        private var INSTANCE: CartDB? = null

        fun getInstance(context: Context): CartDB {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CartDB::class.java,
                        CART_DB
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