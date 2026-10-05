package com.rogue.gymquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "workouts")
data class WorkoutEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: Long,

    val startedAt: Long,

    val finishedAt: Long? = null,

    val notes: String? = null,

    val status: WorkoutStatus,

    val createdAt: Long,

    val updatedAt: Long
)
