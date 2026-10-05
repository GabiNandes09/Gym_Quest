package com.rogue.gymquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = MuscleGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["muscleGroupId"]
        )
    ],
    indices = [Index("muscleGroupId")]
)
data class ExerciseEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val muscleGroupId: Long,

    val equipment: String? = null,

    val notes: String? = null,

    val exerciseType: ExerciseType,

    val createdAt: Long,

    val updatedAt: Long
)
