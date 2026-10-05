package com.rogue.gymquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "workout_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"]
        )
    ],
    indices = [Index("workoutId"), Index("exerciseId")]
)
data class WorkoutSetEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val workoutId: Long,

    val exerciseId: Long,

    val order: Int,

    val setType: SetType,

    val weight: Double? = null,

    val reps: Int? = null,

    val durationSeconds: Int? = null,

    val distanceMeters: Double? = null,

    val restTimeSeconds: Int,

    val rpe: Int? = null,

    val notes: String? = null,

    val supersetGroupId: Long? = null,

    val createdAt: Long,

    val updatedAt: Long
)
