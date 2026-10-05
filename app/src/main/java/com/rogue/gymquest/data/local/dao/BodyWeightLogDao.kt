package com.rogue.gymquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rogue.gymquest.data.local.entity.BodyWeightLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyWeightLogDao {

    @Insert
    suspend fun insert(log: BodyWeightLogEntity): Long

    @Update
    suspend fun update(log: BodyWeightLogEntity)

    @Query("SELECT * FROM body_weight_logs ORDER BY date DESC")
    fun getAll(): Flow<List<BodyWeightLogEntity>>

    @Query("DELETE FROM body_weight_logs WHERE id = :id")
    suspend fun delete(id: Long)
}
