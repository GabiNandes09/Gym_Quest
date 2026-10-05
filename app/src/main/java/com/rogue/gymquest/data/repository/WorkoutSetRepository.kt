package com.rogue.gymquest.data.repository

import com.rogue.gymquest.data.local.dao.WorkoutSetDao
import com.rogue.gymquest.data.local.entity.SetType
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity
import com.rogue.gymquest.data.local.entity.WorkoutSetWithExercise
import kotlinx.coroutines.flow.Flow

class WorkoutSetRepository(
    private val workoutSetDao: WorkoutSetDao
) {

    fun getByWorkoutId(workoutId: Long): Flow<List<WorkoutSetEntity>> =
        workoutSetDao.getByWorkoutId(workoutId)

    fun getByWorkoutIdWithExercise(workoutId: Long): Flow<List<WorkoutSetWithExercise>> =
        workoutSetDao.getByWorkoutIdWithExercise(workoutId)

    fun getByExerciseId(exerciseId: Long): Flow<List<WorkoutSetEntity>> =
        workoutSetDao.getByExerciseId(exerciseId)

    fun getPersonalRecord(exerciseId: Long): Flow<WorkoutSetEntity?> =
        workoutSetDao.getPersonalRecord(exerciseId)

    suspend fun getLastForExercise(exerciseId: Long): WorkoutSetEntity? =
        workoutSetDao.getLastForExercise(exerciseId)

    suspend fun add(
        workoutId: Long,
        exerciseId: Long,
        order: Int,
        setType: SetType,
        weight: Double?,
        reps: Int?,
        durationSeconds: Int?,
        distanceMeters: Double?,
        restTimeSeconds: Int,
        rpe: Int?,
        notes: String?,
        supersetGroupId: Long?
    ): Long {
        val now = System.currentTimeMillis()
        return workoutSetDao.insert(
            WorkoutSetEntity(
                workoutId = workoutId,
                exerciseId = exerciseId,
                order = order,
                setType = setType,
                weight = weight,
                reps = reps,
                durationSeconds = durationSeconds,
                distanceMeters = distanceMeters,
                restTimeSeconds = restTimeSeconds,
                rpe = rpe,
                notes = notes?.trim()?.ifBlank { null },
                supersetGroupId = supersetGroupId,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    suspend fun update(workoutSet: WorkoutSetEntity) =
        workoutSetDao.update(workoutSet.copy(updatedAt = System.currentTimeMillis()))

    suspend fun delete(id: Long) =
        workoutSetDao.delete(id)

    suspend fun deleteByWorkoutId(workoutId: Long) =
        workoutSetDao.deleteByWorkoutId(workoutId)
}
