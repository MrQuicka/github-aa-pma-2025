package com.example.myapp013aeducationgame.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapp013aeducationgame.database.entities.UserScore

@Dao
interface UserScoreDao {
    @Insert
    suspend fun insert(userScore: UserScore)

    @Query("SELECT * FROM user_scores WHERE userId = :userId ORDER BY score DESC")
    suspend fun getScoresForUser(userId: Int): List<UserScore>
}
