package com.ikhokha.core.persistance.room.tables.users

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UsersDAO {
    @Insert
    fun insert(usersTable: UsersTable)

    @Update
    fun update(usersTable: UsersTable)

    @Delete
    fun delete(usersTable: UsersTable)

    @Query("SELECT * FROM users WHERE id = :key")
    fun get(key: String): UsersTable?

    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    fun getLastUser(): UsersTable?

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsersLiveData(): LiveData<List<UsersTable>>

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): List<UsersTable>?

    @Query("DELETE  FROM users")
    fun clear()
}