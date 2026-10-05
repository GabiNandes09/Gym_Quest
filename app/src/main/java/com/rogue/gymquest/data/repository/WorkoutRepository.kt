package com.rogue.gymquest.data.repository

import com.rogue.gymquest.data.local.dao.WorkoutDao
import com.rogue.gymquest.data.local.entity.WorkoutEntity
import com.rogue.gymquest.data.local.entity.WorkoutStatus
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao
) {

    fun getById(id: Long): Flow<WorkoutEntity?> =
        workoutDao.getById(id)

    fun getCompleted(): Flow<List<WorkoutEntity>> =
        workoutDao.getCompleted()

    suspend fun getInProgress(): WorkoutEntity? =
        workoutDao.getInProgress()

    suspend fun getLastCompleted(): WorkoutEntity? =
        workoutDao.getLastCompleted()

    suspend fun startNew(date: Long = System.currentTimeMillis()): Long {
        val now = System.currentTimeMillis()
        return workoutDao.insert(
            WorkoutEntity(
                date = date,
                startedAt = now,
                status = WorkoutStatus.IN_PROGRESS,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    suspend fun finish(workout: WorkoutEntity) {
        val now = System.currentTimeMillis()
        workoutDao.update(
            workout.copy(finishedAt = now, status = WorkoutStatus.COMPLETED, updatedAt = now)
        )
    }

    suspend fun updateNotes(workout: WorkoutEntity, notes: String?) {
        workoutDao.update(
            workout.copy(notes = notes?.trim()?.ifBlank { null }, updatedAt = System.currentTimeMillis())
        )
    }

    suspend fun delete(id: Long) =
        workoutDao.delete(id)
}
