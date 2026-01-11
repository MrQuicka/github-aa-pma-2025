package com.example.myapp011fittrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityAdapter(private val activities: List<String>) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.activityTitle.text = activities[position]
    }

    override fun getItemCount() = activities.size

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityTitle: TextView = itemView.findViewById(R.id.tv_activity_title)
    }
}