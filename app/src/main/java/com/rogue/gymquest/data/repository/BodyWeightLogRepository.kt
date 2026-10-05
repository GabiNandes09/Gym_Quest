package com.rogue.gymquest.data.repository

import com.rogue.gymquest.data.local.dao.BodyWeightLogDao
import com.rogue.gymquest.data.local.entity.BodyWeightLogEntity
import kotlinx.coroutines.flow.Flow

class BodyWeightLogRepository(
    private val bodyWeightLogDao: BodyWeightLogDao
) {

    fun getAll(): Flow<List<BodyWeightLogEntity>> =
        bodyWeightLogDao.getAll()

    suspend fun log(date: Long, weight: Double): Long {
        val now = System.currentTimeMillis()
        return bodyWeightLogDao.insert(
            BodyWeightLogEntity(date = date, weight = weight, createdAt = now, updatedAt = now)
        )
    }

    suspend fun update(log: BodyWeightLogEntity) =
        bodyWeightLogDao.update(log.copy(updatedAt = System.currentTimeMillis()))

    suspend fun delete(id: Long) =
        bodyWeightLogDao.delete(id)
}
