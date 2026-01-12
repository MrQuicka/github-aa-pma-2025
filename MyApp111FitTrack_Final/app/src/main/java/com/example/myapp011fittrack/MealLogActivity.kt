package com.example.myapp011fittrack

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapp011fittrack.data.FitTrackDatabase
import com.example.myapp011fittrack.data.Meal
import kotlinx.coroutines.launch

class MealLogActivity : AppCompatActivity() {

    private lateinit var database: FitTrackDatabase
    private lateinit var mealNameEditText: EditText
    private lateinit var mealCaloriesEditText: EditText
    private lateinit var mealNotesEditText: EditText
    private lateinit var saveButton: Button

    private var mealId: Long? = null
    private var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_log)

        database = FitTrackDatabase.getDatabase(this)

        initViews()

        // Zkontrolovat, zda jsme v editačním režimu
        mealId = intent.getLongExtra("meal_id", -1L).takeIf { it != -1L }
        editMode = intent.getBooleanExtra("edit_mode", false)

        if (editMode && mealId != null) {
            title = "Upravit jídlo"
            saveButton.text = "Aktualizovat"
            loadMeal(mealId!!)
        } else {
            title = "Přidat jídlo"
        }

        saveButton.setOnClickListener {
            if (editMode && mealId != null) {
                updateMeal()
            } else {
                saveMeal()
            }
        }
    }

    private fun initViews() {
        mealNameEditText = findViewById(R.id.et_meal_name)
        mealCaloriesEditText = findViewById(R.id.et_meal_calories)
        mealNotesEditText = findViewById(R.id.et_meal_notes)
        saveButton = findViewById(R.id.btn_save_meal)
    }

    private fun loadMeal(id: Long) {
        lifecycleScope.launch {
            val meal = database.mealDao().getMealById(id)
            if (meal != null) {
                mealNameEditText.setText(meal.name)
                mealCaloriesEditText.setText(meal.calories.toString())
                mealNotesEditText.setText(meal.notes)
            } else {
                Toast.makeText(
                    this@MealLogActivity,
                    "Jídlo nenalezeno",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun saveMeal() {
        val mealName = mealNameEditText.text.toString().trim()
        val mealCalories = mealCaloriesEditText.text.toString().trim()
        val mealNotes = mealNotesEditText.text.toString().trim()

        if (mealName.isEmpty()) {
            mealNameEditText.error = "Zadejte název jídla"
            return
        }

        if (mealCalories.isEmpty()) {
            mealCaloriesEditText.error = "Zadejte kalorie"
            return
        }

        val calories = mealCalories.toIntOrNull()
        if (calories == null || calories < 0) {
            mealCaloriesEditText.error = "Zadejte platné kalorie"
            return
        }

        val meal = Meal(
            name = mealName,
            calories = calories,
            notes = mealNotes
        )

        lifecycleScope.launch {
            try {
                database.mealDao().insertMeal(meal)
                Toast.makeText(
                    this@MealLogActivity,
                    "Jídlo uloženo",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@MealLogActivity,
                    "Chyba při ukládání: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateMeal() {
        val mealName = mealNameEditText.text.toString().trim()
        val mealCalories = mealCaloriesEditText.text.toString().trim()
        val mealNotes = mealNotesEditText.text.toString().trim()

        if (mealName.isEmpty()) {
            mealNameEditText.error = "Zadejte název jídla"
            return
        }

        if (mealCalories.isEmpty()) {
            mealCaloriesEditText.error = "Zadejte kalorie"
            return
        }

        val calories = mealCalories.toIntOrNull()
        if (calories == null || calories < 0) {
            mealCaloriesEditText.error = "Zadejte platné kalorie"
            return
        }

        lifecycleScope.launch {
            try {
                val existingMeal = database.mealDao().getMealById(mealId!!)
                if (existingMeal != null) {
                    val updatedMeal = existingMeal.copy(
                        name = mealName,
                        calories = calories,
                        notes = mealNotes
                    )
                    database.mealDao().updateMeal(updatedMeal)
                    Toast.makeText(
                        this@MealLogActivity,
                        "Jídlo aktualizováno",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MealLogActivity,
                    "Chyba při aktualizaci: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
