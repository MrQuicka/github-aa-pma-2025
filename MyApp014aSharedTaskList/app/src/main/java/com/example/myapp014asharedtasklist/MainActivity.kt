package com.example.myapp014asharedtasklist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val tasksCollection = db.collection("tasks")
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)

        taskAdapter = TaskAdapter(tasks, { task -> toggleTaskCompleted(task) }, { task -> showEditTaskDialog(task) }, { task -> deleteTask(task) })
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
            addTask()
        }

        loadTasks()
    }

    private fun loadTasks() {
        tasksCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            tasks.clear()
            for (document in snapshots!!) {
                val task = document.toObject(Task::class.java).copy(id = document.id)
                tasks.add(task)
            }
            taskAdapter.notifyDataSetChanged()
        }
    }

    private fun addTask() {
        val text = editText.text.toString()
        if (text.isNotEmpty()) {
            val task = Task(text = text)
            tasksCollection.add(task)
            editText.text.clear()
        }
    }

    private fun toggleTaskCompleted(task: Task) {
        task.id?.let {
            tasksCollection.document(it).update("completed", !task.isCompleted)
        }
    }

    private fun showEditTaskDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_edit_task, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)

        editText.setText(task.text)

        builder.setView(dialogLayout)
        builder.setPositiveButton("Uložit") { _, _ ->
            val newText = editText.text.toString()
            if (newText.isNotEmpty()) {
                task.id?.let {
                    tasksCollection.document(it).update("text", newText)
                }
            }
        }
        builder.setNegativeButton("Zrušit", null)
        builder.show()
    }

    private fun deleteTask(task: Task) {
        task.id?.let {
            tasksCollection.document(it).delete()
        }
    }
}