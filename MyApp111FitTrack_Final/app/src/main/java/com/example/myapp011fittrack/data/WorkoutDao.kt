package com.example.myapp011fittrack.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): Workout?

    @Query("SELECT * FROM workouts ORDER BY timestamp DESC")
    fun getAllWorkouts(): LiveData<List<Workout>>

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()
}
