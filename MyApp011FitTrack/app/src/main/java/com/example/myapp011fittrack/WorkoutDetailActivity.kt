package com.example.myapp011fittrack

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WorkoutDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_detail)

        findViewById<Button>(R.id.btn_save_workout).setOnClickListener {
            val workoutName = findViewById<EditText>(R.id.et_workout_name).text.toString()
            val workoutDuration = findViewById<EditText>(R.id.et_workout_duration).text.toString()
            val workoutNotes = findViewById<EditText>(R.id.et_workout_notes).text.toString()

            if (workoutName.isNotBlank()) {
                val sharedPreferences = getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val activities = sharedPreferences.getStringSet("activities", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                activities.add("Cvičení: $workoutName, $workoutDuration min, Poznámky: $workoutNotes")
                editor.putStringSet("activities", activities)
                editor.apply()

                Toast.makeText(this, "Cvičení uloženo", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Zadejte název cvičení", Toast.LENGTH_SHORT).show()
            }
        }
    }
}