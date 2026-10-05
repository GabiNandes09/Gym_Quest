package com.rogue.gymquest.presentation.viewmodel.states

import com.rogue.gymquest.data.local.entity.ExerciseWithMuscleGroup

data class ExerciseListState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val exercises: List<ExerciseWithMuscleGroup> = emptyList(),
    val errorMessage: String? = null
)
