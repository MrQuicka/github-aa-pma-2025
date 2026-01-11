package com.example.myapp017xmassapp.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp017xmassapp.R

class CalendarAdapter(private val onDayClick: (Int) -> Unit) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private val days = (1..24).toList()
    private var unlockedDays: Set<String> = emptySet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day, unlockedDays.contains(day.toString()))
    }

    override fun getItemCount(): Int = days.size

    fun setUnlockedDays(unlocked: Set<String>) {
        unlockedDays = unlocked
        notifyDataSetChanged() // This is not optimal, but fine for a start
    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayNumber: TextView = itemView.findViewById(R.id.day_number)

        fun bind(day: Int, isUnlocked: Boolean) {
            dayNumber.text = day.toString()
            // Here we will later change the visual state based on whether the day is locked/unlocked
            itemView.alpha = if (isUnlocked) 1.0f else 0.5f // Simple visual cue

            itemView.setOnClickListener {
                onDayClick(day)
            }
        }
    }
}