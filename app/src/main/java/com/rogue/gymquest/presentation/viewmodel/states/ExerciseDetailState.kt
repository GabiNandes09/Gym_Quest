package com.rogue.gymquest.presentation.viewmodel.states

import com.rogue.gymquest.data.local.entity.ExerciseType
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity

data class ExerciseDetailState(
    val isLoading: Boolean = true,
    val exerciseName: String = "",
    val muscleGroupName: String = "",
    val equipment: String? = null,
    val notes: String? = null,
    val exerciseType: ExerciseType = ExerciseType.WEIGHT_REPS,
    val personalRecordWeight: Double? = null,
    val history: List<WorkoutSetEntity> = emptyList()
)
