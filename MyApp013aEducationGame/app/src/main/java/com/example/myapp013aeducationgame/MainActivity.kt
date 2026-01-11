package com.example.myapp013aeducationgame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp013aeducationgame.data.InMemoryDatabase
import com.example.myapp013aeducationgame.data.User
import com.example.myapp013aeducationgame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentUser: User? = null

    private val startGameActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            updateHighScore()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener { 
            val name = binding.nameEditText.text.toString()
            if (name.isNotBlank()) {
                var user = InMemoryDatabase.getUserByName(name)
                if (user == null) {
                    user = InMemoryDatabase.insertUser(name)
                }
                currentUser = user
                updateHighScore()

                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("USER_ID", currentUser?.id)
                startGameActivity.launch(intent)

            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateHighScore() {
        currentUser?.let {
            val scores = InMemoryDatabase.getScoresForUser(it.id)
            val highScore = scores.maxByOrNull { it.score }?.score ?: 0
            binding.highScoreTextView.text = "High Score: $highScore"
            binding.highScoreTextView.visibility = View.VISIBLE
        } ?: run {
            binding.highScoreTextView.visibility = View.GONE
        }
    }
}
