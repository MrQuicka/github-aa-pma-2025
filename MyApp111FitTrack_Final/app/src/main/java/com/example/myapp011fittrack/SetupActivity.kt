package com.example.myapp011fittrack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapp011fittrack.data.FitTrackDatabase
import com.example.myapp011fittrack.data.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.launch

class SetupActivity : AppCompatActivity() {

    private lateinit var database: FitTrackDatabase
    private lateinit var maleRadioButton: MaterialRadioButton
    private lateinit var femaleRadioButton: MaterialRadioButton
    private lateinit var heightEditText: TextInputEditText
    private lateinit var weightEditText: TextInputEditText
    private lateinit var ageEditText: TextInputEditText
    private lateinit var saveButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        database = FitTrackDatabase.getDatabase(this)

        initViews()

        // Zkontrolovat, zda jsme v editačním režimu
        val editMode = intent.getBooleanExtra("edit_mode", false)
        if (editMode) {
            title = "Upravit profil"
            saveButton.text = "Uložit změny"
            loadUserData()
        }

        setupListeners()
    }

    private fun initViews() {
        maleRadioButton = findViewById(R.id.maleRadioButton)
        femaleRadioButton = findViewById(R.id.femaleRadioButton)
        heightEditText = findViewById(R.id.heightEditText)
        weightEditText = findViewById(R.id.weightEditText)
        ageEditText = findViewById(R.id.ageEditText)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            val user = database.userDao().getUserSync()
            if (user != null) {
                if (user.gender == "male") {
                    maleRadioButton.isChecked = true
                } else {
                    femaleRadioButton.isChecked = true
                }
                heightEditText.setText(user.heightCm.toString())
                weightEditText.setText(user.weightKg.toString())
                ageEditText.setText(user.ageYears.toString())
            }
        }
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveUserData()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val height = heightEditText.text.toString()
        val weight = weightEditText.text.toString()
        val age = ageEditText.text.toString()

        if (height.isEmpty()) {
            heightEditText.error = "Zadejte výšku"
            return false
        }

        if (weight.isEmpty()) {
            weightEditText.error = "Zadejte váhu"
            return false
        }

        if (age.isEmpty()) {
            ageEditText.error = "Zadejte věk"
            return false
        }

        val heightValue = height.toDoubleOrNull()
        if (heightValue == null || heightValue <= 0 || heightValue > 300) {
            heightEditText.error = "Neplatná výška (1-300 cm)"
            return false
        }

        val weightValue = weight.toDoubleOrNull()
        if (weightValue == null || weightValue <= 0 || weightValue > 500) {
            weightEditText.error = "Neplatná váha (1-500 kg)"
            return false
        }

        val ageValue = age.toIntOrNull()
        if (ageValue == null || ageValue <= 0 || ageValue > 150) {
            ageEditText.error = "Neplatný věk (1-150 let)"
            return false
        }

        return true
    }

    private fun saveUserData() {
        val gender = if (maleRadioButton.isChecked) "male" else "female"
        val height = heightEditText.text.toString().toDouble()
        val weight = weightEditText.text.toString().toDouble()
        val age = ageEditText.text.toString().toInt()

        val user = User(
            id = 1,
            gender = gender,
            heightCm = height,
            weightKg = weight,
            ageYears = age
        )

        lifecycleScope.launch {
            try {
                database.userDao().insertUser(user.withCalculatedBMR())

                // Uložit flag, že setup byl dokončen
                getSharedPreferences("FitTrackPrefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("setup_completed", true)
                    .apply()

                Toast.makeText(
                    this@SetupActivity,
                    "Profil byl úspěšně vytvořen!",
                    Toast.LENGTH_SHORT
                ).show()

                // Přejít na hlavní aktivitu
                val intent = Intent(this@SetupActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@SetupActivity,
                    "Chyba při ukládání: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
