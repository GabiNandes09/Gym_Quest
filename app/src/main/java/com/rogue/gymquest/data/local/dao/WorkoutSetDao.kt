package com.rogue.gymquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity
import com.rogue.gymquest.data.local.entity.WorkoutSetWithExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSetDao {

    @Insert
    suspend fun insert(workoutSet: WorkoutSetEntity): Long

    @Update
    suspend fun update(workoutSet: WorkoutSetEntity)

    @Query("SELECT * FROM workout_sets")
    fun getAll(): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE workoutId = :workoutId ORDER BY `order` ASC")
    fun getByWorkoutId(workoutId: Long): Flow<List<WorkoutSetEntity>>

    @Query(
        """
        SELECT s.id AS id, s.exerciseId AS exerciseId, e.name AS exerciseName,
               s.`order` AS `order`, s.setType AS setType, s.weight AS weight,
               s.reps AS reps, s.durationSeconds AS durationSeconds,
               s.distanceMeters AS distanceMeters, s.restTimeSeconds AS restTimeSeconds,
               s.completed AS completed, s.notes AS notes
        FROM workout_sets s
        INNER JOIN exercises e ON e.id = s.exerciseId
        WHERE s.workoutId = :workoutId
        ORDER BY s.`order` ASC
        """
    )
    fun getByWorkoutIdWithExercise(workoutId: Long): Flow<List<WorkoutSetWithExercise>>

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

    @Query("UPDATE workout_sets SET supersetGroupId = :groupId WHERE id = :id")
    suspend fun updateSupersetGroup(id: Long, groupId: Long)

    @Query("UPDATE workout_sets SET weight = :weight, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateWeight(id: Long, weight: Double?, updatedAt: Long)

    @Query("UPDATE workout_sets SET reps = :reps, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateReps(id: Long, reps: Int?, updatedAt: Long)

    @Query("UPDATE workout_sets SET completed = :completed, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateCompleted(id: Long, completed: Boolean, updatedAt: Long)

    @Query("UPDATE workout_sets SET `order` = `order` + 1 WHERE workoutId = :workoutId AND `order` >= :fromOrder")
    suspend fun shiftOrdersFrom(workoutId: Long, fromOrder: Int)

    @Query("DELETE FROM workout_sets WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM workout_sets WHERE workoutId = :workoutId")
    suspend fun deleteByWorkoutId(workoutId: Long)
}
