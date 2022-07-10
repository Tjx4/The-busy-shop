package com.ikhokha.core.persistance.room.tables.items

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemsDAO {
    @Insert
    fun insert(itemsTable: ItemsTable)

    @Update
    fun update(itemsTable: ItemsTable)

    @Delete
    fun delete(itemsTable: ItemsTable)

    @Query("SELECT * FROM items WHERE id = :key")
    fun get(key: String): ItemsTable?

    @Query("SELECT * FROM items ORDER BY id DESC LIMIT 1")
    fun getLastUser(): ItemsTable?

    @Query("SELECT * FROM items ORDER BY id DESC")
    fun getAllItemsLiveData(): LiveData<List<ItemsTable>>

    @Query("SELECT * FROM items ORDER BY id DESC")
    fun getAllItems(): List<ItemsTable>?

    @Query("DELETE  FROM items")
    fun clear()
}