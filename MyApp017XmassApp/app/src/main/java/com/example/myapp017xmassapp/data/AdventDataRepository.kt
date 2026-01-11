package com.example.myapp017xmassapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AdventDataRepository(private val context: Context) {

    private object PreferencesKeys {
        val UNLOCKED_DAYS_KEY = stringSetPreferencesKey("unlocked_days")
        val VIEWED_DAYS_KEY = stringSetPreferencesKey("viewed_days")
        val COMPLETED_TASKS_KEY = stringSetPreferencesKey("completed_tasks")
        val QUIZ_POINTS_KEY = intPreferencesKey("quiz_points")
        val LAST_OPEN_DATE_KEY = longPreferencesKey("last_open_date")
        val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        val DEBUG_MODE_KEY = booleanPreferencesKey("debug_mode")
        val THEME_KEY = stringPreferencesKey("theme")
        val FAVORITE_RECIPES_KEY = stringSetPreferencesKey("favorite_recipes")
    }

    val unlockedDaysFlow: Flow<Set<String>> = context.dataStore.data
        .map {
            it[PreferencesKeys.UNLOCKED_DAYS_KEY] ?: emptySet()
        }

    suspend fun unlockDay(day: Int) {
        context.dataStore.edit {
            val currentUnlocked = it[PreferencesKeys.UNLOCKED_DAYS_KEY] ?: emptySet()
            it[PreferencesKeys.UNLOCKED_DAYS_KEY] = currentUnlocked + day.toString()
        }
    }

    suspend fun isDayUnlocked(day: Int): Boolean {
        val unlockedDays = unlockedDaysFlow.first()
        return unlockedDays.contains(day.toString())
    }

    suspend fun setDayViewed(day: Int) {
        context.dataStore.edit {
            val currentViewed = it[PreferencesKeys.VIEWED_DAYS_KEY] ?: emptySet()
            it[PreferencesKeys.VIEWED_DAYS_KEY] = currentViewed + day.toString()
        }
    }

    val viewedDaysFlow: Flow<Set<String>> = context.dataStore.data
        .map {
            it[PreferencesKeys.VIEWED_DAYS_KEY] ?: emptySet()
        }

    val debugModeFlow: Flow<Boolean> = context.dataStore.data
        .map {
            it[PreferencesKeys.DEBUG_MODE_KEY] ?: false
        }
}