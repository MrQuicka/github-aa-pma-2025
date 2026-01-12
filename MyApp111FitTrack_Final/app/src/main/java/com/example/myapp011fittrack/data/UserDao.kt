package com.example.myapp011fittrack.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user WHERE id = 1 LIMIT 1")
    fun getUser(): LiveData<User?>

    @Query("SELECT * FROM user WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): User?

    @Query("DELETE FROM user")
    suspend fun deleteUser()
}
