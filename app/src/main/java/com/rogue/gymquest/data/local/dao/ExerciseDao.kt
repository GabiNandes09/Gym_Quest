package com.rogue.gymquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rogue.gymquest.data.local.entity.ExerciseEntity
import com.rogue.gymquest.data.local.entity.ExerciseWithMuscleGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert
    suspend fun insert(exercise: ExerciseEntity): Long

    @Update
    suspend fun update(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Long): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): ExerciseEntity?

    @Query("SELECT * FROM exercises")
    fun getAllRaw(): Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT e.id AS id, e.name AS name,
               e.muscleGroupId AS muscleGroupId, m.name AS muscleGroupName,
               e.equipment AS equipment, e.notes AS notes,
               e.exerciseType AS exerciseType
        FROM exercises e
        INNER JOIN muscle_groups m ON m.id = e.muscleGroupId
        ORDER BY e.name ASC
        """
    )
    fun getAllWithMuscleGroup(): Flow<List<ExerciseWithMuscleGroup>>

    @Query(
        """
        SELECT e.id AS id, e.name AS name,
               e.muscleGroupId AS muscleGroupId, m.name AS muscleGroupName,
               e.equipment AS equipment, e.notes AS notes,
               e.exerciseType AS exerciseType
        FROM exercises e
        INNER JOIN muscle_groups m ON m.id = e.muscleGroupId
        WHERE e.name LIKE '%' || :query || '%'
        ORDER BY e.name ASC
        """
    )
    fun search(query: String): Flow<List<ExerciseWithMuscleGroup>>

    @Query("SELECT COUNT(*) FROM workout_sets WHERE exerciseId = :id")
    suspend fun countSetsUsing(id: Long): Int

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun delete(id: Long)
}
