package com.example.myapp013aeducationgame.data

data class Question(
    val id: Int,
    val questionText: String,
    val correctAnswer: String,
    val incorrectAnswer1: String,
    val incorrectAnswer2: String,
    val incorrectAnswer3: String
)
