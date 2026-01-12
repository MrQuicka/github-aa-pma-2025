package com.example.myapp011fittrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
