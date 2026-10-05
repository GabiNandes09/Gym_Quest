package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.repository.WorkoutRepository
import com.rogue.gymquest.presentation.viewmodel.states.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepository.getCompleted().collect { workouts ->
                _state.value = HomeState(
                    isLoading = false,
                    totalWorkouts = workouts.size,
                    lastWorkoutDate = workouts.firstOrNull()?.date
                )
            }
        }
    }
}
