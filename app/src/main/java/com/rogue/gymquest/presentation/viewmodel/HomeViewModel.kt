package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.repository.WorkoutRepository
import com.rogue.gymquest.presentation.viewmodel.states.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val inProgress = workoutRepository.getInProgress()
            _state.value = HomeState(
                isLoading = false,
                hasInProgressWorkout = inProgress != null,
                inProgressWorkoutId = inProgress?.id
            )
        }
    }
}
