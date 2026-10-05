package com.rogue.gymquest.presentation.viewmodel.states

import com.rogue.gymquest.data.local.entity.WorkoutEntity
import com.rogue.gymquest.data.local.entity.WorkoutSetWithExercise

data class SetGroup(
    val exerciseId: Long,
    val exerciseName: String,
    val sets: List<WorkoutSetWithExercise>
)

data class WorkoutDetailState(
    val isLoading: Boolean = true,
    val workout: WorkoutEntity? = null,
    val setGroups: List<SetGroup> = emptyList(),
    val isProcessing: Boolean = false,
    val startedWorkoutId: Long? = null,
    val shouldNavigateBack: Boolean = false,
    val errorMessage: String? = null
)
