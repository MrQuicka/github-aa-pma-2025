package com.example.myapp011fittrack.data

/**
 * Společný typ pro zobrazení workoutů a jídel v seznamu
 */
sealed class Activity {
    abstract val id: Long
    abstract val timestamp: Long
    abstract val displayText: String

    data class WorkoutActivity(
        override val id: Long,
        val workout: Workout
    ) : Activity() {
        override val timestamp: Long get() = workout.timestamp
        override val displayText: String
            get() = "Cvičení: ${workout.name}, ${workout.durationMinutes} min, Poznámky: ${workout.notes}"
    }

    data class MealActivity(
        override val id: Long,
        val meal: Meal
    ) : Activity() {
        override val timestamp: Long get() = meal.timestamp
        override val displayText: String
            get() = "Jídlo: ${meal.name}, ${meal.calories} kcal, Poznámky: ${meal.notes}"
    }
}
