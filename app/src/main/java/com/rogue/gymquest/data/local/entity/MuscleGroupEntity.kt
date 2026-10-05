package com.rogue.gymquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "muscle_groups")
data class MuscleGroupEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val createdAt: Long,

    val updatedAt: Long
)
