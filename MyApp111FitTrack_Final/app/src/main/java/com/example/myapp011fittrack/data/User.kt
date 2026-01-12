package com.example.myapp011fittrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id: Int = 1, // Pouze jeden uživatel v aplikaci
    val gender: String, // "male" nebo "female"
    val heightCm: Double, // Výška v cm
    val weightKg: Double, // Váha v kg
    val ageYears: Int, // Věk v letech
    val bmr: Double = 0.0 // Bazální metabolismus (vypočítá se automaticky)
) {
    /**
     * Vypočítá BMR podle Harris-Benedict vzorce (revize 1984)
     * Muži: 88.362 + (13.397 × váha v kg) + (4.799 × výška v cm) - (5.677 × věk v letech)
     * Ženy: 447.593 + (9.247 × váha v kg) + (3.098 × výška v cm) - (4.330 × věk v letech)
     */
    fun calculateBMR(): Double {
        return if (gender == "male") {
            88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * ageYears)
        } else {
            447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * ageYears)
        }
    }

    /**
     * Vytvoří kopii User s vypočítaným BMR
     */
    fun withCalculatedBMR(): User {
        return this.copy(bmr = calculateBMR())
    }
}
