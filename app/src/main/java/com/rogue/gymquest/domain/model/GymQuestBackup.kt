package com.rogue.gymquest.domain.model

import com.rogue.gymquest.data.local.entity.BodyWeightLogEntity
import com.rogue.gymquest.data.local.entity.ExerciseEntity
import com.rogue.gymquest.data.local.entity.MuscleGroupEntity
import com.rogue.gymquest.data.local.entity.WorkoutEntity
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity
import kotlinx.serialization.Serializable

const val BACKUP_SCHEMA_VERSION = 1

@Serializable
data class GymQuestBackup(
    val schemaVersion: Int,
    val exportedAt: Long,
    val muscleGroups: List<MuscleGroupEntity>,
    val exercises: List<ExerciseEntity>,
    val workouts: List<WorkoutEntity>,
    val workoutSets: List<WorkoutSetEntity>,
    val bodyWeightLogs: List<BodyWeightLogEntity> = emptyList()
)
