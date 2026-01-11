package com.example.myapp013aeducationgame

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp013aeducationgame.data.InMemoryDatabase
import com.example.myapp013aeducationgame.data.Question
import com.example.myapp013aeducationgame.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private var currentQuestionIndex = 0
    private var score = 0
    private var userId: Int = -1

    private val questions = InMemoryDatabase.getAllQuestions().shuffled()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        showQuestion()

        binding.answerButton1.setOnClickListener { checkAnswer(binding.answerButton1.text.toString()) }
        binding.answerButton2.setOnClickListener { checkAnswer(binding.answerButton2.text.toString()) }
        binding.answerButton3.setOnClickListener { checkAnswer(binding.answerButton3.text.toString()) }
        binding.answerButton4.setOnClickListener { checkAnswer(binding.answerButton4.text.toString()) }
    }

    private fun showQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            binding.questionTextView.text = question.questionText

            val answers = listOf(question.correctAnswer, question.incorrectAnswer1, question.incorrectAnswer2, question.incorrectAnswer3).shuffled()

            binding.answerButton1.text = answers[0]
            binding.answerButton2.text = answers[1]
            binding.answerButton3.text = answers[2]
            binding.answerButton4.text = answers[3]
        } else {
            endGame()
        }
    }

    private fun checkAnswer(selectedAnswer: String) {
        val question = questions[currentQuestionIndex]
        if (selectedAnswer == question.correctAnswer) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect! The correct answer was ${question.correctAnswer}", Toast.LENGTH_SHORT).show()
        }
        currentQuestionIndex++
        showQuestion()
    }

    private fun endGame() {
        InMemoryDatabase.insertScore(userId, score)
        Toast.makeText(this, "Game Over! Your score: $score", Toast.LENGTH_LONG).show()
        setResult(Activity.RESULT_OK)
        finish()
    }
}
