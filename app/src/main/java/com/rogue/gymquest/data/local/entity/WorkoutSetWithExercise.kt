package com.rogue.gymquest.data.local.entity

data class WorkoutSetWithExercise(
    val id: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val order: Int,
    val setType: SetType,
    val weight: Double?,
    val reps: Int?,
    val durationSeconds: Int?,
    val distanceMeters: Double?,
    val restTimeSeconds: Int,
    val completed: Boolean,
    val notes: String?
)
