package com.rogue.gymquest.presentation.viewmodel.states

import com.rogue.gymquest.data.local.entity.WorkoutEntity

data class WorkoutsState(
    val isLoading: Boolean = true,
    val hasInProgressWorkout: Boolean = false,
    val inProgressWorkoutId: Long? = null,
    val routineTemplates: List<WorkoutEntity> = emptyList(),
    val exercisePreviewByTemplateId: Map<Long, String> = emptyMap(),
    val isStarting: Boolean = false,
    val startedWorkoutId: Long? = null,
    val errorMessage: String? = null
)
