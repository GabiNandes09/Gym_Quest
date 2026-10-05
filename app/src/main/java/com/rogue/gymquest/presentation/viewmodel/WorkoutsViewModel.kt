package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.repository.WorkoutRepository
import com.rogue.gymquest.presentation.viewmodel.states.WorkoutsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutsViewModel(
    workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepository.getInProgressFlow().collect { inProgress ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    hasInProgressWorkout = inProgress != null,
                    inProgressWorkoutId = inProgress?.id
                )
            }
        }
        viewModelScope.launch {
            workoutRepository.getRoutineTemplates().collect { templates ->
                _state.value = _state.value.copy(routineTemplates = templates)
            }
        }
    }
}
