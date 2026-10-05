package com.rogue.gymquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rogue.gymquest.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert
    suspend fun insert(workout: WorkoutEntity): Long

    @Update
    suspend fun update(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getById(id: Long): Flow<WorkoutEntity?>

    @Query("SELECT * FROM workouts")
    fun getAll(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE status = 'IN_PROGRESS' LIMIT 1")
    suspend fun getInProgress(): WorkoutEntity?

    @Query("SELECT * FROM workouts WHERE status = 'IN_PROGRESS' LIMIT 1")
    fun getInProgressFlow(): Flow<WorkoutEntity?>

    @Query("SELECT * FROM workouts WHERE status = 'COMPLETED' ORDER BY date DESC")
    fun getCompleted(): Flow<List<WorkoutEntity>>

    @Query(
        """
        SELECT * FROM workouts w
        WHERE w.name IS NOT NULL AND w.status = 'COMPLETED'
        AND w.id = (
            SELECT w2.id FROM workouts w2
            WHERE w2.name = w.name AND w2.status = 'COMPLETED'
            ORDER BY w2.date DESC, w2.id DESC
            LIMIT 1
        )
        ORDER BY w.date DESC
        """
    )
    fun getLatestPerRoutineName(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE status = 'COMPLETED' ORDER BY date DESC LIMIT 1")
    suspend fun getLastCompleted(): WorkoutEntity?

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun delete(id: Long)
}
