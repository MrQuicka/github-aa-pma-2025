package com.example.myapp010ahadejcislo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var randomNumber = 0
    private lateinit var guessEditText: EditText
    private lateinit var checkButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        guessEditText = findViewById(R.id.guessEditText)
        checkButton = findViewById(R.id.checkButton)

        generateNewNumber()

        checkButton.setOnClickListener {
            checkGuess()
        }
    }

    private fun generateNewNumber() {
        randomNumber = Random.nextInt(1, 11)
    }

    private fun checkGuess() {
        val guessText = guessEditText.text.toString()
        if (guessText.isEmpty()) {
            Toast.makeText(this, "Zadej prosím číslo", Toast.LENGTH_SHORT).show()
            return
        }

        val guess = guessText.toInt()
        if (guess == randomNumber) {
            Toast.makeText(this, "Správně!", Toast.LENGTH_SHORT).show()
            generateNewNumber()
            guessEditText.text.clear()
        } else {
            Toast.makeText(this, "Vedle!", Toast.LENGTH_SHORT).show()
        }
    }
}
