package com.example.myapp004moreactivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp004moreactivities.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = intent.getStringExtra("extra_text")
        binding.textInfo.text = text

        binding.transferButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("result_text", binding.editText.text.toString())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.button2.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("extra_text_from_second", binding.editText.text.toString())
            startActivity(intent)
        }

        binding.button3.setOnClickListener {
            finish()
        }
    }
}
