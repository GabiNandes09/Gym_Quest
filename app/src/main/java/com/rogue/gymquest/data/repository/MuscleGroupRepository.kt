package com.rogue.gymquest.data.repository

import com.rogue.gymquest.data.local.dao.MuscleGroupDao
import com.rogue.gymquest.data.local.entity.MuscleGroupEntity
import kotlinx.coroutines.flow.Flow

class MuscleGroupRepository(
    private val muscleGroupDao: MuscleGroupDao
) {

    fun getAll(): Flow<List<MuscleGroupEntity>> =
        muscleGroupDao.getAll()

    suspend fun getById(id: Long): MuscleGroupEntity? =
        muscleGroupDao.getById(id)

    suspend fun create(name: String): Long {
        val now = System.currentTimeMillis()
        return muscleGroupDao.insert(
            MuscleGroupEntity(name = name.trim(), createdAt = now, updatedAt = now)
        )
    }

    suspend fun rename(muscleGroup: MuscleGroupEntity, newName: String) {
        muscleGroupDao.update(
            muscleGroup.copy(name = newName.trim(), updatedAt = System.currentTimeMillis())
        )
    }

    suspend fun isInUse(id: Long): Boolean =
        muscleGroupDao.countExercisesUsing(id) > 0

    suspend fun delete(id: Long) =
        muscleGroupDao.delete(id)
}
