package com.example.myapp001linearlayout

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // vstupy
        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etSurname = findViewById<TextInputEditText>(R.id.etSurname)
        val etCity = findViewById<TextInputEditText>(R.id.etCity)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val etAddress = findViewById<TextInputEditText>(R.id.etAddress)
        val etAge = findViewById<TextInputEditText>(R.id.etAge)

        // výstupní pole (TextView) – v XML je id "textView"
        val tvInformation = findViewById<TextView>(R.id.textView)

        // tlačítka – v XML: btnRandom = Odeslat, btnSend = Vymazat
        val btnSend = findViewById<MaterialButton>(R.id.btnRandom)
        val btnClear = findViewById<MaterialButton>(R.id.btnSend)

        btnSend.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val surname = etSurname.text?.toString()?.trim().orEmpty()
            val city = etCity.text?.toString()?.trim().orEmpty()
            val phone = etPhone.text?.toString()?.trim().orEmpty()
            val address = etAddress.text?.toString()?.trim().orEmpty()
            val age = etAge.text?.toString()?.trim().orEmpty()

            // Jednoduchá kontrola – případně klidně smaž
            if (name.isEmpty() || surname.isEmpty() || city.isEmpty() ||
                phone.isEmpty() || address.isEmpty() || age.isEmpty()
            ) {
                tvInformation.text = "Vyplň všechna pole."
                return@setOnClickListener
            }

            // jen vypsat zadané údaje do TextView (víceřádkově)
            val output = """
                Jméno: $name
                Příjmení: $surname
                Město: $city
                Telefon: $phone
                Adresa: $address
                Věk: $age
            """.trimIndent()

            tvInformation.text = output
        }

        btnClear.setOnClickListener {
            etName.setText("")
            etSurname.setText("")
            etCity.setText("")
            etPhone.setText("")
            etAddress.setText("")
            etAge.setText("")
            tvInformation.text = ""
        }
    }
}
