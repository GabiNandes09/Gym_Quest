package com.rogue.gymquest.data.local.entity

data class ExerciseWithMuscleGroup(
    val id: Long,
    val name: String,
    val muscleGroupId: Long,
    val muscleGroupName: String,
    val equipment: String?,
    val notes: String?,
    val exerciseType: ExerciseType
)
