package com.example.myapp007fragments

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

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

        val buttonFragment1 = findViewById<Button>(R.id.button_fragment1)
        val buttonFragment2 = findViewById<Button>(R.id.button_fragment2)

        buttonFragment1.setOnClickListener {
            replaceFragment(FirstFragment())
        }

        buttonFragment2.setOnClickListener {
            replaceFragment(SecondFragment())
        }

        // Zobrazí výchozí fragment
        if (savedInstanceState == null) {
            replaceFragment(FirstFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}