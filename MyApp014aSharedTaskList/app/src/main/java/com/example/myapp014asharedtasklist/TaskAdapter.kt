package com.example.myapp014asharedtasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskCompleted: (Task) -> Unit,
    private val onEditTask: (Task) -> Unit,
    private val onDeleteTask: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, onTaskCompleted, onEditTask, onDeleteTask)
    }

    override fun getItemCount() = tasks.size

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        private val menuButton: View = itemView.findViewById(R.id.menu_button) // Assuming a menu button exists

        fun bind(
            task: Task,
            onTaskCompleted: (Task) -> Unit,
            onEditTask: (Task) -> Unit,
            onDeleteTask: (Task) -> Unit
        ) {
            textView.text = task.text

            // Fix for checkbox bug
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = task.isCompleted
            checkBox.setOnCheckedChangeListener { _, _ -> onTaskCompleted(task) }

            menuButton.setOnClickListener { view ->
                val popup = PopupMenu(itemView.context, view)
                popup.inflate(R.menu.task_item_menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit -> {
                            onEditTask(task)
                            true
                        }
                        R.id.action_delete -> {
                            onDeleteTask(task)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
}