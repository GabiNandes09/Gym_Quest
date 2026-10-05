package com.rogue.gymquest.data.repository

import com.rogue.gymquest.data.local.dao.ExerciseDao
import com.rogue.gymquest.data.local.entity.ExerciseEntity
import com.rogue.gymquest.data.local.entity.ExerciseType
import com.rogue.gymquest.data.local.entity.ExerciseWithMuscleGroup
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(
    private val exerciseDao: ExerciseDao
) {

    fun getAll(): Flow<List<ExerciseWithMuscleGroup>> =
        exerciseDao.getAllWithMuscleGroup()

    fun search(query: String): Flow<List<ExerciseWithMuscleGroup>> =
        exerciseDao.search(query.trim())

    suspend fun getById(id: Long): ExerciseEntity? =
        exerciseDao.getById(id)

    suspend fun create(
        name: String,
        muscleGroupId: Long,
        exerciseType: ExerciseType,
        equipment: String?,
        notes: String?
    ): Long {
        val now = System.currentTimeMillis()
        return exerciseDao.insert(
            ExerciseEntity(
                name = name.trim(),
                muscleGroupId = muscleGroupId,
                equipment = equipment?.trim()?.ifBlank { null },
                notes = notes?.trim()?.ifBlank { null },
                exerciseType = exerciseType,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    suspend fun update(exercise: ExerciseEntity) =
        exerciseDao.update(exercise.copy(updatedAt = System.currentTimeMillis()))

    suspend fun isInUse(id: Long): Boolean =
        exerciseDao.countSetsUsing(id) > 0

    suspend fun delete(id: Long) =
        exerciseDao.delete(id)
}
