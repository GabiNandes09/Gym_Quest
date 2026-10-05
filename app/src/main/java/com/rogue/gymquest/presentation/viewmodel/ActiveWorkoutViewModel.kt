package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ActiveWorkoutViewModel(
    workoutRepository: WorkoutRepository
) : ViewModel() {

    val inProgressWorkout = workoutRepository.getInProgressFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
