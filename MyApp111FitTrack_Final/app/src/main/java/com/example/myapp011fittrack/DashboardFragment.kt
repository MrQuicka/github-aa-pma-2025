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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter
    private lateinit var repository: FitTrackRepository
    private lateinit var bmrValueText: TextView
    private lateinit var userInfoText: TextView
    private lateinit var bmrDetailButton: MaterialButton
    private lateinit var caloriesConsumedText: TextView
    private lateinit var caloriesRemainingText: TextView
    private lateinit var bmrTargetText: TextView
    private lateinit var calorieProgressBar: LinearProgressIndicator
    private lateinit var calorieStatusText: TextView
    private lateinit var activityStreakIcon: TextView
    private lateinit var activityStreakText: TextView
    private lateinit var activityStreakMotivation: TextView
    private lateinit var activityStatusBadge: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
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

        view.findViewById<FloatingActionButton>(R.id.fab_add_activity).setOnClickListener {
            AddActivityDialogFragment().show(parentFragmentManager, "AddActivityDialog")
        }

        bmrDetailButton.setOnClickListener {
            val intent = Intent(requireContext(), BmrDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.rv_activities)
        bmrValueText = view.findViewById(R.id.bmrValueText)
        userInfoText = view.findViewById(R.id.userInfoText)
        bmrDetailButton = view.findViewById(R.id.bmrDetailButton)
        caloriesConsumedText = view.findViewById(R.id.caloriesConsumedText)
        caloriesRemainingText = view.findViewById(R.id.caloriesRemainingText)
        bmrTargetText = view.findViewById(R.id.bmrTargetText)
        calorieProgressBar = view.findViewById(R.id.calorieProgressBar)
        calorieStatusText = view.findViewById(R.id.calorieStatusText)
        activityStreakIcon = view.findViewById(R.id.activityStreakIcon)
        activityStreakText = view.findViewById(R.id.activityStreakText)
        activityStreakMotivation = view.findViewById(R.id.activityStreakMotivation)
        activityStatusBadge = view.findViewById(R.id.activityStatusBadge)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        activityAdapter = ActivityAdapter(
            activities = emptyList(),
            onEditClick = { activity -> handleEdit(activity) },
            onDeleteClick = { activity -> handleDelete(activity) }
        )
        recyclerView.adapter = activityAdapter
    }

    private fun observeData() {
        var currentBmr = 0.0

        // Sledovat uživatelská data
        repository.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                currentBmr = user.bmr
                val bmr = String.format("%.0f", user.bmr)
                bmrValueText.text = "$bmr kcal/den"
                bmrTargetText.text = "$bmr kcal"

                val genderText = if (user.gender == "male") "Muž" else "Žena"
                userInfoText.text = "$genderText, ${user.ageYears} let, ${user.heightCm.toInt()} cm, ${user.weightKg.toInt()} kg"

                // Aktualizovat kalorie při změně BMR
                repository.allMeals.observe(viewLifecycleOwner) { meals ->
                    updateCalorieInfo(meals, currentBmr)
                }
            } else {
                bmrValueText.text = "-- kcal/den"
                userInfoText.text = "Profil nenalezen"
                bmrTargetText.text = "0 kcal"
            }
        }

        // Sledovat aktivity
        repository.allActivities.observe(viewLifecycleOwner) { activities ->
            // Filtrovat pouze dnešní aktivity pro zobrazení
            val todayActivities = getTodayActivities(activities)
            activityAdapter = ActivityAdapter(
                activities = todayActivities,
                onEditClick = { activity -> handleEdit(activity) },
                onDeleteClick = { activity -> handleDelete(activity) }
            )
            recyclerView.adapter = activityAdapter

            // Aktualizovat motivační kartu
            updateDailyActivityStatus(todayActivities)
        }

        // Sledovat cvičení pro motivační kartu
        repository.allWorkouts.observe(viewLifecycleOwner) { workouts ->
            val todayWorkouts = getTodayWorkouts(workouts)
            updateDailyActivityStatus(getTodayActivities(repository.allActivities.value ?: emptyList()))
        }
    }

    private fun getTodayWorkouts(workouts: List<com.example.myapp011fittrack.data.Workout>): List<com.example.myapp011fittrack.data.Workout> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        return workouts.filter { it.timestamp >= startOfDay }
    }

    private fun updateDailyActivityStatus(todayActivities: List<Activity>) {
        // Zjistit, zda dnes uživatel cvičil
        val todayWorkouts = todayActivities.filterIsInstance<Activity.WorkoutActivity>()
        val hasWorkedOutToday = todayWorkouts.isNotEmpty()

        if (hasWorkedOutToday) {
            // Uživatel dnes cvičil - zobrazit úspěch
            activityStreakIcon.text = "💪"
            activityStreakText.text = "Skvělá práce! Dnes jste cvičili"

            val totalTime = todayWorkouts.sumOf { it.workout.durationMinutes }
            activityStreakMotivation.text = "Celkem ${totalTime} minut pohybové aktivity"

            activityStatusBadge.visibility = View.VISIBLE
            activityStatusBadge.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            // Uživatel dnes ještě necvičil - motivovat
            activityStreakIcon.text = "🔥"
            activityStreakText.text = "Dnes jste ještě necvičili"

            val motivationalMessages = listOf(
                "Zahajte svůj den pohybem!",
                "Už jen 10 minut udělá rozdíl",
                "Ваše tělo vám poděkuje",
                "Budete se cítit skvěle!",
                "Každý den je nová příležitost"
            )
            activityStreakMotivation.text = motivationalMessages.random()

            activityStatusBadge.visibility = View.GONE
        }
    }

    private fun getTodayActivities(activities: List<Activity>): List<Activity> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        return activities.filter { it.timestamp >= startOfDay }
    }

    private fun updateCalorieInfo(meals: List<com.example.myapp011fittrack.data.Meal>, bmr: Double) {
        // Vypočítat dnešní kalorie
        val todayMeals = getTodayMeals(meals)
        val totalCalories = todayMeals.sumOf { it.calories }

        caloriesConsumedText.text = "$totalCalories kcal"

        val remaining = bmr - totalCalories
        caloriesRemainingText.text = "${String.format("%.0f", remaining)} kcal"

        // Nastavit progress bar
        val progress = if (bmr > 0) {
            ((totalCalories / bmr) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
        calorieProgressBar.progress = progress

        // Aktualizovat status text a barvu
        when {
            remaining < 0 -> {
                calorieStatusText.text = "Překročili jste denní limit o ${String.format("%.0f", -remaining)} kcal"
                caloriesRemainingText.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            }
            remaining < bmr * 0.2 -> {
                calorieStatusText.text = "Blížíte se k dennímu limitu"
                caloriesRemainingText.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
            }
            else -> {
                calorieStatusText.text = "Máte ještě ${String.format("%.0f", remaining)} kcal k dispozici"
                caloriesRemainingText.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            }
        }
    }

    private fun getTodayMeals(meals: List<com.example.myapp011fittrack.data.Meal>): List<com.example.myapp011fittrack.data.Meal> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        return meals.filter { it.timestamp >= startOfDay }
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
