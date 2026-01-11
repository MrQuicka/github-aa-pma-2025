package com.example.myapp011fittrack

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_activities)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<FloatingActionButton>(R.id.fab_add_activity).setOnClickListener {
            AddActivityDialogFragment().show(parentFragmentManager, "AddActivityDialog")
        }
    }

    override fun onResume() {
        super.onResume()
        loadActivities()
    }

    private fun loadActivities() {
        val sharedPreferences = requireActivity().getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE)
        val activities = sharedPreferences.getStringSet("activities", setOf())?.toList() ?: emptyList()
        activityAdapter = ActivityAdapter(activities.reversed())
        recyclerView.adapter = activityAdapter
    }
}