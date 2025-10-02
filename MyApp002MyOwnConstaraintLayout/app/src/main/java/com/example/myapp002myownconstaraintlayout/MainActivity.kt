package com.example.myapp002myownconstaraintlayout

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvResult: TextView = findViewById(R.id.tvResult)
        val btnRandom: Button = findViewById(R.id.btnRandom)

        btnRandom.setOnClickListener {
            val anoNe = if (kotlin.random.Random.nextBoolean()) "Ano" else "Ne"
            tvResult.text = anoNe
        }
    }
}
