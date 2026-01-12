package com.example.myapp011fittrack

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapp011fittrack.data.FitTrackDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class BmrDetailActivity : AppCompatActivity() {

    private lateinit var database: FitTrackDatabase
    private lateinit var bmrResultText: TextView
    private lateinit var userDataText: TextView
    private lateinit var calculationText: TextView
    private lateinit var closeButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmr_detail)

        database = FitTrackDatabase.getDatabase(this)

        initViews()
        loadUserData()

        closeButton.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        bmrResultText = findViewById(R.id.bmrResultText)
        userDataText = findViewById(R.id.userDataText)
        calculationText = findViewById(R.id.calculationText)
        closeButton = findViewById(R.id.closeButton)
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            val user = database.userDao().getUserSync()

            if (user != null) {
                val bmr = String.format("%.2f", user.bmr)
                bmrResultText.text = "${String.format("%.0f", user.bmr)} kcal/den"

                val genderText = if (user.gender == "male") "Muž" else "Žena"
                userDataText.text = "$genderText, ${user.ageYears} let, ${user.heightCm.toInt()} cm, ${user.weightKg.toInt()} kg"

                // Zobrazit detailní výpočet
                val calculation = if (user.gender == "male") {
                    val part1 = 88.362
                    val part2 = 13.397 * user.weightKg
                    val part3 = 4.799 * user.heightCm
                    val part4 = 5.677 * user.ageYears

                    "Váš výpočet (muž):\n\n" +
                            "88.362\n" +
                            "+ (13.397 × ${String.format("%.1f", user.weightKg)}) = + ${String.format("%.2f", part2)}\n" +
                            "+ (4.799 × ${String.format("%.1f", user.heightCm)}) = + ${String.format("%.2f", part3)}\n" +
                            "- (5.677 × ${user.ageYears}) = - ${String.format("%.2f", part4)}\n" +
                            "─────────────────────\n" +
                            "= ${String.format("%.2f", user.bmr)} kcal/den"
                } else {
                    val part1 = 447.593
                    val part2 = 9.247 * user.weightKg
                    val part3 = 3.098 * user.heightCm
                    val part4 = 4.330 * user.ageYears

                    "Váš výpočet (žena):\n\n" +
                            "447.593\n" +
                            "+ (9.247 × ${String.format("%.1f", user.weightKg)}) = + ${String.format("%.2f", part2)}\n" +
                            "+ (3.098 × ${String.format("%.1f", user.heightCm)}) = + ${String.format("%.2f", part3)}\n" +
                            "- (4.330 × ${user.ageYears}) = - ${String.format("%.2f", part4)}\n" +
                            "─────────────────────\n" +
                            "= ${String.format("%.2f", user.bmr)} kcal/den"
                }

                calculationText.text = calculation
            } else {
                bmrResultText.text = "-- kcal/den"
                userDataText.text = "Uživatelský profil nebyl nalezen"
                calculationText.text = "Prosím, dokončete úvodní nastavení"
            }
        }
    }
}
