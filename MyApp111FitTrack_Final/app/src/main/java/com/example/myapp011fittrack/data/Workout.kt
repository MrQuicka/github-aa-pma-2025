package com.example.myapp011fittrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val durationMinutes: Int,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val caloriesBurned: Double = 0.0 // Volitelné: kalorie spálené během cvičení
)
