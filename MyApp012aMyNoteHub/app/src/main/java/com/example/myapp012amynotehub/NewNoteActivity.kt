package com.example.myapp012amynotehub

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class NewNoteActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editDescription: EditText
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)
        editTitle = findViewById(R.id.edit_title)
        editDescription = findViewById(R.id.edit_description)

        if (intent.hasExtra(EXTRA_REPLY_ID)) {
            noteId = intent.getIntExtra(EXTRA_REPLY_ID, -1)
            editTitle.setText(intent.getStringExtra(EXTRA_REPLY_TITLE))
            editDescription.setText(intent.getStringExtra(EXTRA_REPLY_DESCRIPTION))
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (editTitle.text.isEmpty() || editDescription.text.isEmpty()) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val title = editTitle.text.toString()
                val description = editDescription.text.toString()

                replyIntent.putExtra(EXTRA_REPLY_ID, noteId)
                replyIntent.putExtra(EXTRA_REPLY_TITLE, title)
                replyIntent.putExtra(EXTRA_REPLY_DESCRIPTION, description)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY_ID = "com.example.myapp012amynotehub.REPLY_ID"
        const val EXTRA_REPLY_TITLE = "com.example.myapp012amynotehub.REPLY_TITLE"
        const val EXTRA_REPLY_DESCRIPTION = "com.example.myapp012amynotehub.REPLY_DESCRIPTION"
    }
}
