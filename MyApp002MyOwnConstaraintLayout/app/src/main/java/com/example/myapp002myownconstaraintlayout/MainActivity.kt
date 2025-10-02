package com.example.myapp002myownconstaraintlayout

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd

class MainActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etOrder: EditText
    private lateinit var btnProcess: Button
    private lateinit var btnDelete: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etOrder = findViewById(R.id.etOrder)
        btnProcess = findViewById(R.id.btnProcess)
        btnDelete = findViewById(R.id.btnDelete)
        progressBar = findViewById(R.id.progressBar)
        tvResult = findViewById(R.id.tvResult)

        btnProcess.setOnClickListener {
            // Schovej klávesnici (volitelné)
            hideKeyboard()

            // Disable akce během „běhu“
            setInputsEnabled(false)
            tvResult.text = ""

            // Zobraz progress bar a animuj 3 sekundy (3000 ms)
            progressBar.progress = 0
            progressBar.visibility = ProgressBar.VISIBLE

            val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
            animator.duration = 3000L
            animator.doOnEnd {
                progressBar.visibility = ProgressBar.GONE

                val name = etName.text.toString().trim()
                val surname = etSurname.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val address = etAddress.text.toString().trim()
                val order = etOrder.text.toString().trim()

                val summary = """
                    Name: $name
                    Surname: $surname
                    Phone: $phone
                    Address: $address
                    Order: $order
                """.trimIndent()

                tvResult.text = summary
                setInputsEnabled(true)
            }
            animator.start()
        }

        btnDelete.setOnClickListener {
            etName.text?.clear()
            etSurname.text?.clear()
            etPhone.text?.clear()
            etAddress.text?.clear()
            etOrder.text?.clear()
            tvResult.text = ""
            progressBar.clearAnimation()
            progressBar.progress = 0
            progressBar.visibility = ProgressBar.GONE
            setInputsEnabled(true)
        }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        etName.isEnabled = enabled
        etSurname.isEnabled = enabled
        etPhone.isEnabled = enabled
        etAddress.isEnabled = enabled
        etOrder.isEnabled = enabled
        btnProcess.isEnabled = enabled
        btnDelete.isEnabled = enabled
    }

    private fun hideKeyboard() {
        currentFocus?.let { view ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
