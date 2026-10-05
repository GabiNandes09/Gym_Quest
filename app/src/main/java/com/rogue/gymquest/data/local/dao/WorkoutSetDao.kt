package com.rogue.gymquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSetDao {

    @Insert
    suspend fun insert(workoutSet: WorkoutSetEntity): Long

    @Update
    suspend fun update(workoutSet: WorkoutSetEntity)

    @Query("SELECT * FROM workout_sets WHERE workoutId = :workoutId ORDER BY `order` ASC")
    fun getByWorkoutId(workoutId: Long): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE exerciseId = :exerciseId ORDER BY createdAt DESC")
    fun getByExerciseId(exerciseId: Long): Flow<List<WorkoutSetEntity>>

    @Query(
        """
        SELECT * FROM workout_sets
        WHERE exerciseId = :exerciseId
        ORDER BY createdAt DESC
        LIMIT 1
        """
    )
    suspend fun getLastForExercise(exerciseId: Long): WorkoutSetEntity?

    @Query(
        """
        SELECT * FROM workout_sets
        WHERE exerciseId = :exerciseId AND weight IS NOT NULL
        ORDER BY weight DESC
        LIMIT 1
        """
    )
    fun getPersonalRecord(exerciseId: Long): Flow<WorkoutSetEntity?>

    @Query("DELETE FROM workout_sets WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM workout_sets WHERE workoutId = :workoutId")
    suspend fun deleteByWorkoutId(workoutId: Long)
}
