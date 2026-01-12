package com.example.myapp011fittrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp011fittrack.data.Activity
import com.google.android.material.button.MaterialButton

class ActivityAdapter(
    private val activities: List<Activity>,
    private val onEditClick: (Activity) -> Unit,
    private val onDeleteClick: (Activity) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.activityTitle.text = activity.displayText

        holder.btnEdit.setOnClickListener {
            onEditClick(activity)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(activity)
        }
    }

    override fun getItemCount() = activities.size

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityTitle: TextView = itemView.findViewById(R.id.tv_activity_title)
        val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }
}
