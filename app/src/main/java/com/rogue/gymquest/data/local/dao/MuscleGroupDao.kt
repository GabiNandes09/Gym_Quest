package com.rogue.gymquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rogue.gymquest.data.local.entity.MuscleGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MuscleGroupDao {

    @Insert
    suspend fun insert(muscleGroup: MuscleGroupEntity): Long

    @Update
    suspend fun update(muscleGroup: MuscleGroupEntity)

    @Query("SELECT * FROM muscle_groups ORDER BY name ASC")
    fun getAll(): Flow<List<MuscleGroupEntity>>

    @Query("SELECT * FROM muscle_groups WHERE id = :id")
    suspend fun getById(id: Long): MuscleGroupEntity?

    @Query("SELECT * FROM muscle_groups WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): MuscleGroupEntity?

    @Query("SELECT COUNT(*) FROM exercises WHERE muscleGroupId = :id")
    suspend fun countExercisesUsing(id: Long): Int

    @Query("DELETE FROM muscle_groups WHERE id = :id")
    suspend fun delete(id: Long)
}
