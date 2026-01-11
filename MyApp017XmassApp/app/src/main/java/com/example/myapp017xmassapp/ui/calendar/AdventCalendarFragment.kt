package com.example.myapp017xmassapp.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapp017xmassapp.databinding.FragmentAdventCalendarBinding
import kotlinx.coroutines.launch

class AdventCalendarFragment : Fragment() {

    private var _binding: FragmentAdventCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdventCalendarViewModel by viewModels()
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdventCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarAdapter = CalendarAdapter { day ->
            viewModel.onDayClicked(day)
        }
        binding.calendarRecyclerView.adapter = calendarAdapter

        viewModel.unlockedDays.observe(viewLifecycleOwner) {
            calendarAdapter.setUnlockedDays(it)
        }

        lifecycleScope.launch {
            viewModel.toastMessage.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}