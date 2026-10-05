package com.rogue.gymquest.presentation.viewmodel.states

import com.rogue.gymquest.data.local.entity.WorkoutEntity

data class HistoryState(
    val isLoading: Boolean = true,
    val workouts: List<WorkoutEntity> = emptyList()
)
