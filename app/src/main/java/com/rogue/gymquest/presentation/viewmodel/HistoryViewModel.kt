package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.repository.WorkoutRepository
import com.rogue.gymquest.presentation.viewmodel.states.HistoryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepository.getCompleted().collect { workouts ->
                _state.value = HistoryState(isLoading = false, workouts = workouts)
            }
        }
    }
}
