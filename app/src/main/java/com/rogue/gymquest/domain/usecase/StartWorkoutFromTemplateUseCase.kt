package com.rogue.gymquest.domain.usecase

import com.rogue.gymquest.data.local.entity.WorkoutEntity
import com.rogue.gymquest.data.repository.WorkoutRepository
import com.rogue.gymquest.data.repository.WorkoutSetRepository
import kotlinx.coroutines.flow.first

class StartWorkoutFromTemplateUseCase(
    private val workoutRepository: WorkoutRepository,
    private val workoutSetRepository: WorkoutSetRepository
) {

    suspend fun hasInProgressWorkout(): Boolean =
        workoutRepository.getInProgress() != null

    suspend fun start(template: WorkoutEntity): Long {
        val templateSets = workoutSetRepository.getByWorkoutId(template.id).first()
        val newWorkoutId = workoutRepository.startNew(name = template.name)

        templateSets.forEach { set ->
            workoutSetRepository.add(
                workoutId = newWorkoutId,
                exerciseId = set.exerciseId,
                order = set.order,
                setType = set.setType,
                weight = set.weight,
                reps = set.reps,
                durationSeconds = set.durationSeconds,
                distanceMeters = set.distanceMeters,
                restTimeSeconds = set.restTimeSeconds,
                rpe = null,
                notes = null,
                supersetGroupId = null
            )
        }

        return newWorkoutId
    }
}
