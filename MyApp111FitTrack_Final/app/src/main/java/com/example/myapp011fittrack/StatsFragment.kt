package com.example.myapp011fittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapp011fittrack.data.FitTrackDatabase
import com.example.myapp011fittrack.data.FitTrackRepository
import com.example.myapp011fittrack.data.Meal
import com.example.myapp011fittrack.data.Workout
import java.util.Calendar
import java.util.concurrent.TimeUnit

class StatsFragment : Fragment() {

    private lateinit var repository: FitTrackRepository
    private lateinit var totalWorkoutsText: TextView
    private lateinit var totalMealsText: TextView
    private lateinit var totalWorkoutTimeText: TextView
    private lateinit var avgWorkoutTimeText: TextView
    private lateinit var longestWorkoutText: TextView
    private lateinit var totalCaloriesText: TextView
    private lateinit var avgCaloriesText: TextView
    private lateinit var highestCalorieMealText: TextView
    private lateinit var todayWorkoutsText: TextView
    private lateinit var todayMealsText: TextView
    private lateinit var todayCaloriesText: TextView
    private lateinit var todayWorkoutTimeText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
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
        observeData()
    }

    private fun initViews(view: View) {
        totalWorkoutsText = view.findViewById(R.id.totalWorkoutsText)
        totalMealsText = view.findViewById(R.id.totalMealsText)
        totalWorkoutTimeText = view.findViewById(R.id.totalWorkoutTimeText)
        avgWorkoutTimeText = view.findViewById(R.id.avgWorkoutTimeText)
        longestWorkoutText = view.findViewById(R.id.longestWorkoutText)
        totalCaloriesText = view.findViewById(R.id.totalCaloriesText)
        avgCaloriesText = view.findViewById(R.id.avgCaloriesText)
        highestCalorieMealText = view.findViewById(R.id.highestCalorieMealText)
        todayWorkoutsText = view.findViewById(R.id.todayWorkoutsText)
        todayMealsText = view.findViewById(R.id.todayMealsText)
        todayCaloriesText = view.findViewById(R.id.todayCaloriesText)
        todayWorkoutTimeText = view.findViewById(R.id.todayWorkoutTimeText)
    }

    private fun observeData() {
        // Sledovat cvičení
        repository.allWorkouts.observe(viewLifecycleOwner) { workouts ->
            updateWorkoutStats(workouts)
        }

        // Sledovat jídla
        repository.allMeals.observe(viewLifecycleOwner) { meals ->
            updateMealStats(meals)
        }
    }

    private fun updateWorkoutStats(workouts: List<Workout>) {
        // Celkové statistiky cvičení
        totalWorkoutsText.text = workouts.size.toString()

        val totalTime = workouts.sumOf { it.durationMinutes }
        totalWorkoutTimeText.text = "Celkový čas: $totalTime min"

        val avgTime = if (workouts.isNotEmpty()) workouts.map { it.durationMinutes }.average() else 0.0
        avgWorkoutTimeText.text = "Průměrná délka: ${String.format("%.0f", avgTime)} min"

        val longestWorkout = workouts.maxByOrNull { it.durationMinutes }
        longestWorkoutText.text = if (longestWorkout != null) {
            "Nejdelší cvičení: ${longestWorkout.durationMinutes} min (${longestWorkout.name})"
        } else {
            "Nejdelší cvičení: 0 min"
        }

        // Dnešní statistiky cvičení
        val todayWorkouts = getTodayWorkouts(workouts)
        todayWorkoutsText.text = "Cvičení dnes: ${todayWorkouts.size}"

        val todayWorkoutTime = todayWorkouts.sumOf { it.durationMinutes }
        todayWorkoutTimeText.text = "Čas cvičení dnes: $todayWorkoutTime min"
    }

    private fun updateMealStats(meals: List<Meal>) {
        // Celkové statistiky jídel
        totalMealsText.text = meals.size.toString()

        val totalCalories = meals.sumOf { it.calories }
        totalCaloriesText.text = "Celkové kalorie: $totalCalories kcal"

        // Vypočítat průměrné kalorie za den
        val daysCount = calculateDaysSpan(meals)
        val avgCaloriesPerDay = if (daysCount > 0) totalCalories / daysCount else 0
        avgCaloriesText.text = "Průměr za den: $avgCaloriesPerDay kcal"

        val highestMeal = meals.maxByOrNull { it.calories }
        highestCalorieMealText.text = if (highestMeal != null) {
            "Nejvyšší jídlo: ${highestMeal.calories} kcal (${highestMeal.name})"
        } else {
            "Nejvyšší jídlo: 0 kcal"
        }

        // Dnešní statistiky jídel
        val todayMeals = getTodayMeals(meals)
        todayMealsText.text = "Jídla dnes: ${todayMeals.size}"

        val todayCalories = todayMeals.sumOf { it.calories }
        todayCaloriesText.text = "Kalorie dnes: $todayCalories kcal"
    }

    private fun getTodayWorkouts(workouts: List<Workout>): List<Workout> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        return workouts.filter { it.timestamp >= startOfDay }
    }

    private fun getTodayMeals(meals: List<Meal>): List<Meal> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        return meals.filter { it.timestamp >= startOfDay }
    }

    private fun calculateDaysSpan(meals: List<Meal>): Int {
        if (meals.isEmpty()) return 0

        val oldestMeal = meals.minByOrNull { it.timestamp }?.timestamp ?: return 0
        val newestMeal = meals.maxByOrNull { it.timestamp }?.timestamp ?: return 0

        val diffMillis = newestMeal - oldestMeal
        val days = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt() + 1

        return days.coerceAtLeast(1)
    }
}
