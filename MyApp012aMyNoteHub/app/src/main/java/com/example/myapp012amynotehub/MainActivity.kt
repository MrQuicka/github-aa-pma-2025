package com.example.myapp012amynotehub

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp012amynotehub.adapter.NoteListAdapter
import com.example.myapp012amynotehub.database.Note
import com.example.myapp012amynotehub.viewmodel.NoteViewModel
import com.example.myapp012amynotehub.viewmodel.NoteViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as MyNoteHubApplication).repository)
    }

    private val newNoteActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val id = result.data?.getIntExtra(NewNoteActivity.EXTRA_REPLY_ID, -1) ?: -1
                val title = result.data?.getStringExtra(NewNoteActivity.EXTRA_REPLY_TITLE) ?: ""
                val description = result.data?.getStringExtra(NewNoteActivity.EXTRA_REPLY_DESCRIPTION) ?: ""

                if (id != -1) {
                    noteViewModel.update(Note(id, title, description))
                } else {
                    noteViewModel.insert(Note(title = title, description = description))
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NoteListAdapter { note ->
            val intent = Intent(this, NewNoteActivity::class.java)
            intent.putExtra(NewNoteActivity.EXTRA_REPLY_ID, note.id)
            intent.putExtra(NewNoteActivity.EXTRA_REPLY_TITLE, note.title)
            intent.putExtra(NewNoteActivity.EXTRA_REPLY_DESCRIPTION, note.description)
            newNoteActivityLauncher.launch(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        noteViewModel.allNotes.observe(this) {
            notes -> notes?.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewNoteActivity::class.java)
            newNoteActivityLauncher.launch(intent)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // We don't want to support drag and drop
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = adapter.currentList[position]
                noteViewModel.delete(note)

                Snackbar.make(recyclerView, "Note deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO") { noteViewModel.insert(note) }
                    show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
