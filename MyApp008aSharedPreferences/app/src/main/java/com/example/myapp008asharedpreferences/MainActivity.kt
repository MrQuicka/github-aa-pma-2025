package com.example.myapp008asharedpreferences

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextAge: EditText
    private lateinit var checkBoxAdult: CheckBox
    private lateinit var buttonSave: Button
    private lateinit var buttonLoad: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextName = findViewById(R.id.editTextName)
        editTextAge = findViewById(R.id.editTextAge)
        checkBoxAdult = findViewById(R.id.checkBoxAdult)
        buttonSave = findViewById(R.id.buttonSave)
        buttonLoad = findViewById(R.id.buttonLoad)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val age = editTextAge.text.toString().toIntOrNull() ?: 0
            val isAdult = checkBoxAdult.isChecked

            sharedPreferences.edit().apply {
                putString("name", name)
                putInt("age", age)
                putBoolean("isAdult", isAdult)
                apply()
            }
        }

        buttonLoad.setOnClickListener {
            val name = sharedPreferences.getString("name", "")
            val age = sharedPreferences.getInt("age", 0)
            val isAdult = sharedPreferences.getBoolean("isAdult", false)

            editTextName.setText(name)
            editTextAge.setText(age.toString())
            checkBoxAdult.isChecked = isAdult
        }
    }
}
