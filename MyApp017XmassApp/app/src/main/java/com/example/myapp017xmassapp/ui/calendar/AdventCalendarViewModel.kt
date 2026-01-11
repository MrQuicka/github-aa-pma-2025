package com.example.myapp017xmassapp.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapp017xmassapp.data.AdventDataRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class AdventCalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdventDataRepository(application)

    val unlockedDays: LiveData<Set<String>> = repository.unlockedDaysFlow.asLiveData()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun onDayClicked(day: Int) {
        viewModelScope.launch {
            val isDebugMode = repository.debugModeFlow.first()
            if (isDebugMode) {
                repository.unlockDay(day)
                _toastMessage.emit("Day $day unlocked (Debug Mode)")
                return@launch
            }

            val today = Calendar.getInstance()
            val currentMonth = today.get(Calendar.MONTH)
            val currentDayOfMonth = today.get(Calendar.DAY_OF_MONTH)

            if (currentMonth == Calendar.DECEMBER && currentDayOfMonth >= day) {
                if (!repository.isDayUnlocked(day)) {
                    repository.unlockDay(day)
                }
                // Navigate to detail, this will be handled in the fragment
            } else {
                _toastMessage.emit("This day will be available on $day.12.")
            }
        }
    }
}