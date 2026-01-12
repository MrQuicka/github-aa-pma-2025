package com.example.myapp011fittrack.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MealDao {
    @Insert
    suspend fun insertMeal(meal: Meal): Long

    @Update
    suspend fun updateMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: Long): Meal?

    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): LiveData<List<Meal>>

    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()
}
