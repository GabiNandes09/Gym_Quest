package com.rogue.gymquest.presentation.viewmodel.states

import com.rogue.gymquest.data.local.entity.ExerciseType
import com.rogue.gymquest.data.local.entity.MuscleGroupEntity

data class ExerciseFormState(
    val isEditing: Boolean = false,
    val name: String = "",
    val muscleGroups: List<MuscleGroupEntity> = emptyList(),
    val selectedMuscleGroupId: Long? = null,
    val exerciseType: ExerciseType = ExerciseType.WEIGHT_REPS,
    val equipment: String = "",
    val notes: String = "",
    val errorMessage: String? = null,
    val saved: Boolean = false
)
