package com.rogue.gymquest.domain.model

data class ImportResult(
    val muscleGroups: Int,
    val exercises: Int,
    val workouts: Int,
    val workoutSets: Int,
    val bodyWeightLogs: Int
)
