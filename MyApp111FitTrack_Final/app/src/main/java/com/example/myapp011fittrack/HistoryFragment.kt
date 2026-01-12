package com.example.myapp011fittrack

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp011fittrack.data.Activity
import com.example.myapp011fittrack.data.FitTrackDatabase
import com.example.myapp011fittrack.data.FitTrackRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var repository: FitTrackRepository
    private lateinit var profileGenderText: TextView
    private lateinit var profileAgeText: TextView
    private lateinit var profileHeightText: TextView
    private lateinit var profileWeightText: TextView
    private lateinit var profileBmrText: TextView
    private lateinit var editProfileButton: MaterialButton
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = FitTrackDatabase.getDatabase(requireContext())
        repository = FitTrackRepository(
            database.userDao(),
            database.workoutDao(),
            database.mealDao()
        )

        initViews(view)
        setupRecyclerView()
        observeData()

        editProfileButton.setOnClickListener {
            // Otevřít SetupActivity v editačním režimu
            val intent = Intent(requireContext(), SetupActivity::class.java).apply {
                putExtra("edit_mode", true)
            }
            startActivity(intent)
        }
    }

    private fun initViews(view: View) {
        profileGenderText = view.findViewById(R.id.profileGenderText)
        profileAgeText = view.findViewById(R.id.profileAgeText)
        profileHeightText = view.findViewById(R.id.profileHeightText)
        profileWeightText = view.findViewById(R.id.profileWeightText)
        profileBmrText = view.findViewById(R.id.profileBmrText)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
    }

    private fun setupRecyclerView() {
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        activityAdapter = ActivityAdapter(
            activities = emptyList(),
            onEditClick = { activity -> handleEdit(activity) },
            onDeleteClick = { activity -> handleDelete(activity) }
        )
        historyRecyclerView.adapter = activityAdapter
    }

    private fun observeData() {
        // Sledovat uživatelský profil
        repository.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val genderText = if (user.gender == "male") "Muž" else "Žena"
                profileGenderText.text = "Pohlaví: $genderText"
                profileAgeText.text = "Věk: ${user.ageYears} let"
                profileHeightText.text = "Výška: ${user.heightCm.toInt()} cm"
                profileWeightText.text = "Váha: ${user.weightKg.toInt()} kg"
                profileBmrText.text = "BMR: ${String.format("%.0f", user.bmr)} kcal/den"
            } else {
                profileGenderText.text = "Pohlaví: --"
                profileAgeText.text = "Věk: --"
                profileHeightText.text = "Výška: --"
                profileWeightText.text = "Váha: --"
                profileBmrText.text = "BMR: --"
            }
        }

        // Sledovat všechny aktivity (historie)
        repository.allActivities.observe(viewLifecycleOwner) { activities ->
            activityAdapter = ActivityAdapter(
                activities = activities,
                onEditClick = { activity -> handleEdit(activity) },
                onDeleteClick = { activity -> handleDelete(activity) }
            )
            historyRecyclerView.adapter = activityAdapter
        }
    }

    private fun handleEdit(activity: Activity) {
        when (activity) {
            is Activity.WorkoutActivity -> {
                val intent = Intent(requireContext(), WorkoutDetailActivity::class.java).apply {
                    putExtra("workout_id", activity.workout.id)
                    putExtra("edit_mode", true)
                }
                startActivity(intent)
            }
            is Activity.MealActivity -> {
                val intent = Intent(requireContext(), MealLogActivity::class.java).apply {
                    putExtra("meal_id", activity.meal.id)
                    putExtra("edit_mode", true)
                }
                startActivity(intent)
            }
        }
    }

    private fun handleDelete(activity: Activity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Smazat aktivitu?")
            .setMessage("Opravdu chcete smazat tuto aktivitu?")
            .setPositiveButton("Smazat") { _, _ ->
                deleteActivity(activity)
            }
            .setNegativeButton("Zrušit", null)
            .show()
    }

    private fun deleteActivity(activity: Activity) {
        lifecycleScope.launch {
            try {
                when (activity) {
                    is Activity.WorkoutActivity -> {
                        repository.deleteWorkout(activity.workout)
                    }
                    is Activity.MealActivity -> {
                        repository.deleteMeal(activity.meal)
                    }
                }
                Toast.makeText(requireContext(), "Aktivita smazána", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Chyba při mazání: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
