package com.example.myapp003objednavka

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var radioGroupColor: RadioGroup
    private lateinit var imageViewOrder: ImageView
    private lateinit var cbFork: CheckBox
    private lateinit var cbSaddle: CheckBox
    private lateinit var cbWheels: CheckBox
    private lateinit var btnOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ---- Inicializace views ----
        radioGroupColor = findViewById(R.id.rgColour)
        imageViewOrder   = findViewById(R.id.ivOrder)
        cbFork           = findViewById(R.id.cbFork)
        cbSaddle         = findViewById(R.id.cbSaddle)
        cbWheels         = findViewById(R.id.cbWheels)
        btnOrder         = findViewById(R.id.btnOrder)

        // ---- Reakce na výběr barvy (mění obrázek) ----
        radioGroupColor.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbRed -> {
                    imageViewOrder.setImageResource(R.drawable.kolo_cervene)
                    // imageViewOrder.contentDescription = getString(R.string.cervene_kolo_description)
                }
                R.id.rbGrey -> {
                    imageViewOrder.setImageResource(R.drawable.kolo_sede)
                    // imageViewOrder.contentDescription = getString(R.string.sede_kolo_description)
                }
                R.id.rbGreen -> {
                    imageViewOrder.setImageResource(R.drawable.kolo_zelene)
                    // imageViewOrder.contentDescription = getString(R.string.zelene_kolo_description)
                }
                else -> {
                    imageViewOrder.setImageDrawable(null)
                    imageViewOrder.contentDescription = null
                }
            }
        }

        // ---- Po kliknutí na "Objednat" zobraz souhrn v dialogu ----
        btnOrder.setOnClickListener {
            val selectedColorId = radioGroupColor.checkedRadioButtonId
            if (selectedColorId == -1) {
                Toast.makeText(this, "Vyberte prosím barvu kola.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Text z vybraného RadioButtonu (např. "Červené", "Zelené", "Šedé")
            val colorText = findViewById<RadioButton>(selectedColorId).text.toString()

            // Sestavení seznamu vybraných doplňků
            val accessories = mutableListOf<String>()
            if (cbFork.isChecked)   accessories += cbFork.text.toString()   // "Lepší Vidlice"
            if (cbSaddle.isChecked) accessories += cbSaddle.text.toString() // "Lepší Sedlo"
            if (cbWheels.isChecked) accessories += cbWheels.text.toString() // "Lepší Kola"

            val accessoriesText = if (accessories.isEmpty()) "bez doplňků"
            else accessories.joinToString(separator = ", ")

            // Sestavení zprávy
            val message = buildString {
                appendLine("Barva: $colorText")
                append("Doplňky: $accessoriesText")
            }

            // Zobrazení dialogu
            AlertDialog.Builder(this)
                .setTitle("Souhrn objednávky")
                .setMessage(message)
                .setPositiveButton("Potvrdit") { dialog, _ ->
                    // Sem případně dej akci po potvrzení (odeslání objednávky / reset formuláře apod.)
                    dialog.dismiss()
                }
                .setNegativeButton("Zavřít") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}
