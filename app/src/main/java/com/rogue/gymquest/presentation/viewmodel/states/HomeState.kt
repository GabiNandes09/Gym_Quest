package com.rogue.gymquest.presentation.viewmodel.states

data class HomeState(
    val isLoading: Boolean = true,
    val hasInProgressWorkout: Boolean = false,
    val inProgressWorkoutId: Long? = null
)
