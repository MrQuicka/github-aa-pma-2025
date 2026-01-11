package com.example.myapp011fittrack

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MealLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_log)

        findViewById<Button>(R.id.btn_save_meal).setOnClickListener {
            val mealName = findViewById<EditText>(R.id.et_meal_name).text.toString()
            val mealCalories = findViewById<EditText>(R.id.et_meal_calories).text.toString()
            val mealNotes = findViewById<EditText>(R.id.et_meal_notes).text.toString()

            if (mealName.isNotBlank()) {
                val sharedPreferences = getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val activities = sharedPreferences.getStringSet("activities", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                activities.add("Jídlo: $mealName, $mealCalories kcal, Poznámky: $mealNotes")
                editor.putStringSet("activities", activities)
                editor.apply()

                Toast.makeText(this, "Jídlo uloženo", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Zadejte název jídla", Toast.LENGTH_SHORT).show()
            }
        }
    }
}