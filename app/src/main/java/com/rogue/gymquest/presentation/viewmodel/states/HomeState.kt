package com.rogue.gymquest.presentation.viewmodel.states

data class HomeState(
    val isLoading: Boolean = true,
    val totalWorkouts: Int = 0,
    val lastWorkoutDate: Long? = null
)
