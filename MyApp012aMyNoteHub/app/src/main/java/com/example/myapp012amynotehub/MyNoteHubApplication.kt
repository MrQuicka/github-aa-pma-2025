package com.example.myapp012amynotehub

import android.app.Application
import com.example.myapp012amynotehub.database.NoteDatabase
import com.example.myapp012amynotehub.repository.NoteRepository

class MyNoteHubApplication : Application() {
    val database by lazy { NoteDatabase.getDatabase(this) }
    val repository by lazy { NoteRepository(database.noteDao()) }
}
