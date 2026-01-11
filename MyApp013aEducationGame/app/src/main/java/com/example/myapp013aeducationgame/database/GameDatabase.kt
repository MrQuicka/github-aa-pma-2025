package com.example.myapp013aeducationgame.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapp013aeducationgame.database.daos.QuestionDao
import com.example.myapp013aeducationgame.database.daos.UserDao
import com.example.myapp013aeducationgame.database.daos.UserScoreDao
import com.example.myapp013aeducationgame.database.entities.Question
import com.example.myapp013aeducationgame.database.entities.User
import com.example.myapp013aeducationgame.database.entities.UserScore

@Database(entities = [User::class, Question::class, UserScore::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun questionDao(): QuestionDao
    abstract fun userScoreDao(): UserScoreDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
