package com.example.myapp011fittrack

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapp011fittrack.data.FitTrackDatabase
import com.example.myapp011fittrack.data.Workout
import kotlinx.coroutines.launch

class WorkoutDetailActivity : AppCompatActivity() {

    private lateinit var database: FitTrackDatabase
    private lateinit var workoutNameEditText: EditText
    private lateinit var workoutDurationEditText: EditText
    private lateinit var workoutNotesEditText: EditText
    private lateinit var saveButton: Button

    private var workoutId: Long? = null
    private var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_detail)

        database = FitTrackDatabase.getDatabase(this)

        initViews()

        // Zkontrolovat, zda jsme v editačním režimu
        workoutId = intent.getLongExtra("workout_id", -1L).takeIf { it != -1L }
        editMode = intent.getBooleanExtra("edit_mode", false)

        if (editMode && workoutId != null) {
            title = "Upravit cvičení"
            saveButton.text = "Aktualizovat"
            loadWorkout(workoutId!!)
        } else {
            title = "Přidat cvičení"
        }

        saveButton.setOnClickListener {
            if (editMode && workoutId != null) {
                updateWorkout()
            } else {
                saveWorkout()
            }
        }
    }

    private fun initViews() {
        workoutNameEditText = findViewById(R.id.et_workout_name)
        workoutDurationEditText = findViewById(R.id.et_workout_duration)
        workoutNotesEditText = findViewById(R.id.et_workout_notes)
        saveButton = findViewById(R.id.btn_save_workout)
    }

    private fun loadWorkout(id: Long) {
        lifecycleScope.launch {
            val workout = database.workoutDao().getWorkoutById(id)
            if (workout != null) {
                workoutNameEditText.setText(workout.name)
                workoutDurationEditText.setText(workout.durationMinutes.toString())
                workoutNotesEditText.setText(workout.notes)
            } else {
                Toast.makeText(
                    this@WorkoutDetailActivity,
                    "Cvičení nenalezeno",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun saveWorkout() {
        val workoutName = workoutNameEditText.text.toString().trim()
        val workoutDuration = workoutDurationEditText.text.toString().trim()
        val workoutNotes = workoutNotesEditText.text.toString().trim()

        if (workoutName.isEmpty()) {
            workoutNameEditText.error = "Zadejte název cvičení"
            return
        }

        if (workoutDuration.isEmpty()) {
            workoutDurationEditText.error = "Zadejte dobu trvání"
            return
        }

        val durationMinutes = workoutDuration.toIntOrNull()
        if (durationMinutes == null || durationMinutes <= 0) {
            workoutDurationEditText.error = "Zadejte platnou dobu trvání"
            return
        }

        val workout = Workout(
            name = workoutName,
            durationMinutes = durationMinutes,
            notes = workoutNotes
        )

        lifecycleScope.launch {
            try {
                database.workoutDao().insertWorkout(workout)
                Toast.makeText(
                    this@WorkoutDetailActivity,
                    "Cvičení uloženo",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@WorkoutDetailActivity,
                    "Chyba při ukládání: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateWorkout() {
        val workoutName = workoutNameEditText.text.toString().trim()
        val workoutDuration = workoutDurationEditText.text.toString().trim()
        val workoutNotes = workoutNotesEditText.text.toString().trim()

        if (workoutName.isEmpty()) {
            workoutNameEditText.error = "Zadejte název cvičení"
            return
        }

        if (workoutDuration.isEmpty()) {
            workoutDurationEditText.error = "Zadejte dobu trvání"
            return
        }

        val durationMinutes = workoutDuration.toIntOrNull()
        if (durationMinutes == null || durationMinutes <= 0) {
            workoutDurationEditText.error = "Zadejte platnou dobu trvání"
            return
        }

        lifecycleScope.launch {
            try {
                val existingWorkout = database.workoutDao().getWorkoutById(workoutId!!)
                if (existingWorkout != null) {
                    val updatedWorkout = existingWorkout.copy(
                        name = workoutName,
                        durationMinutes = durationMinutes,
                        notes = workoutNotes
                    )
                    database.workoutDao().updateWorkout(updatedWorkout)
                    Toast.makeText(
                        this@WorkoutDetailActivity,
                        "Cvičení aktualizováno",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@WorkoutDetailActivity,
                    "Chyba při aktualizaci: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
