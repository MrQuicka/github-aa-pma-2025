package com.example.myapp011fittrack.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class FitTrackRepository(
    private val userDao: UserDao,
    private val workoutDao: WorkoutDao,
    private val mealDao: MealDao
) {
    // User operace
    val user: LiveData<User?> = userDao.getUser()

    suspend fun insertUser(user: User) {
        userDao.insertUser(user.withCalculatedBMR())
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user.withCalculatedBMR())
    }

    suspend fun getUserSync(): User? = userDao.getUserSync()

    // Workout operace (CRUD)
    val allWorkouts: LiveData<List<Workout>> = workoutDao.getAllWorkouts()

    suspend fun insertWorkout(workout: Workout): Long {
        return workoutDao.insertWorkout(workout)
    }

    suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout)
    }

    suspend fun getWorkoutById(id: Long): Workout? {
        return workoutDao.getWorkoutById(id)
    }

    // Meal operace (CRUD)
    val allMeals: LiveData<List<Meal>> = mealDao.getAllMeals()

    suspend fun insertMeal(meal: Meal): Long {
        return mealDao.insertMeal(meal)
    }

    suspend fun updateMeal(meal: Meal) {
        mealDao.updateMeal(meal)
    }

    suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal)
    }

    suspend fun getMealById(id: Long): Meal? {
        return mealDao.getMealById(id)
    }

    // Kombinovaný seznam všech aktivit
    val allActivities: LiveData<List<Activity>> = MediatorLiveData<List<Activity>>().apply {
        var workouts: List<Workout> = emptyList()
        var meals: List<Meal> = emptyList()

        fun update() {
            val activities = mutableListOf<Activity>()
            activities.addAll(workouts.map { Activity.WorkoutActivity(it.id, it) })
            activities.addAll(meals.map { Activity.MealActivity(it.id, it) })
            value = activities.sortedByDescending { it.timestamp }
        }

        addSource(allWorkouts) {
            workouts = it
            update()
        }

        addSource(allMeals) {
            meals = it
            update()
        }
    }
}
