package com.example.myapp009aimagetoapp

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val button = findViewById<Button>(R.id.button)
        val rotateButton = findViewById<Button>(R.id.rotateButton)
        val grayscaleButton = findViewById<Button>(R.id.grayscaleButton)

        button.setOnClickListener {
            pickImage.launch("image/*")
        }

        rotateButton.setOnClickListener {
            imageView.rotation += 90f
        }

        grayscaleButton.setOnClickListener {
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            imageView.colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
    }
}