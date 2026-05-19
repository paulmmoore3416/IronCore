package com.ironcore.metrics.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironcore.metrics.data.local.entities.Workout
import com.ironcore.metrics.data.local.entities.WorkoutSet
import com.ironcore.metrics.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _currentWorkout = MutableStateFlow<Workout?>(null)
    val currentWorkout = _currentWorkout.asStateFlow()

    private val _workoutSets = MutableStateFlow<List<WorkoutSet>>(emptyList())
    val workoutSets = _workoutSets.asStateFlow()

    fun startWorkout(name: String) {
        viewModelScope.launch {
            val workout = Workout(name = name)
            val id = repository.saveWorkout(workout)
            _currentWorkout.value = workout.copy(id = id)
        }
    }

    fun addSet(
        exerciseId: Long, 
        reps: Int, 
        weight: Float, 
        rpe: Int? = null, 
        restTimeSeconds: Int? = null, 
        notes: String? = null
    ) {
        val workoutId = _currentWorkout.value?.id ?: return
        viewModelScope.launch {
            val set = WorkoutSet(
                workoutId = workoutId,
                exerciseId = exerciseId,
                reps = reps,
                weight = weight,
                rpe = rpe,
                restTimeSeconds = restTimeSeconds,
                notes = notes
            )
            repository.saveWorkoutSet(set)
            _workoutSets.value = _workoutSets.value + set
        }
    }
}
