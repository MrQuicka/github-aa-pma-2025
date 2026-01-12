package com.example.myapp011fittrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Workout::class, Meal::class],
    version = 1,
    exportSchema = false
)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: FitTrackDatabase? = null

        fun getDatabase(context: Context): FitTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitTrackDatabase::class.java,
                    "fittrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
