package com.example.myapp013aeducationgame.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapp013aeducationgame.database.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE name = :name")
    suspend fun getUserByName(name: String): User?
}
