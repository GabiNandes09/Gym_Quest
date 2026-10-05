package com.rogue.gymquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "body_weight_logs")
data class BodyWeightLogEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: Long,

    val weight: Double,

    val createdAt: Long,

    val updatedAt: Long
)
