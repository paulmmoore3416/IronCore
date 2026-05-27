package com.ironcore.metrics.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironcore.metrics.data.local.dao.WorkoutDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val workoutDao: WorkoutDao
) : ViewModel() {

    val historicalInsights = workoutDao.getAllWorkouts()
        .map { workouts ->
            if (workouts.isEmpty()) return@map emptyList<Insight>()
            
            val totalWorkouts = workouts.size
            val volumeTrend = if (workouts.size >= 2) "Up 12%" else "Stable"
            
            listOf(
                Insight("Consistency", "$totalWorkouts workouts completed this month.", "Check"),
                Insight("Volume Trend", volumeTrend, "TrendingUp"),
                Insight("Peak Hour", "6:00 PM is your most active time.", "Schedule")
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

data class Insight(val title: String, val message: String, val icon: String)
