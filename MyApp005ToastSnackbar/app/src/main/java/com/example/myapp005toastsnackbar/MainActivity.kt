package com.example.myapp005toastsnackbar

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnToast = findViewById<Button>(R.id.btnToast)
        val btnSnackbar = findViewById<Button>(R.id.btnSnackbar)
        val mainLayout = findViewById<ConstraintLayout>(R.id.main)

        btnToast.setOnClickListener {
            Toast.makeText(this, "Toto je Toast!", Toast.LENGTH_SHORT).show()
        }

        btnSnackbar.setOnClickListener {
            Snackbar.make(mainLayout, "Toto je Snackbar!", Snackbar.LENGTH_LONG).show()
        }
    }
}