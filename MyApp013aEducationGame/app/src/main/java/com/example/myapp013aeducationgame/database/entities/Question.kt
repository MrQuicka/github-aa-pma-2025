package com.example.myapp013aeducationgame.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val questionText: String,
    val correctAnswer: String,
    val incorrectAnswer1: String,
    val incorrectAnswer2: String,
    val incorrectAnswer3: String
)
