package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity
import com.rogue.gymquest.data.repository.ExerciseRepository
import com.rogue.gymquest.data.repository.MuscleGroupRepository
import com.rogue.gymquest.data.repository.WorkoutSetRepository
import com.rogue.gymquest.presentation.viewmodel.states.ChartPoint
import com.rogue.gymquest.presentation.viewmodel.states.ExerciseDetailState
import com.rogue.gymquest.presentation.viewmodel.states.ExerciseDetailTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseDetailViewModel(
    exerciseRepository: ExerciseRepository,
    muscleGroupRepository: MuscleGroupRepository,
    workoutSetRepository: WorkoutSetRepository,
    exerciseId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseDetailState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val exercise = exerciseRepository.getById(exerciseId)
            if (exercise != null) {
                val muscleGroup = muscleGroupRepository.getById(exercise.muscleGroupId)
                _state.value = _state.value.copy(
                    isLoading = false,
                    exerciseName = exercise.name,
                    muscleGroupName = muscleGroup?.name ?: "",
                    equipment = exercise.equipment,
                    notes = exercise.notes,
                    exerciseType = exercise.exerciseType
                )
            }
        }

        viewModelScope.launch {
            workoutSetRepository.getByExerciseId(exerciseId).collect { history ->
                _state.value = _state.value.copy(
                    history = history,
                    maxWeight = history.maxOfOrNull { it.weight ?: 0.0 }?.takeIf { it > 0 },
                    bestEstimated1RM = estimate1RM(history),
                    bestSetVolumeLabel = bestSetVolumeLabel(history),
                    bestSessionVolume = bestSessionVolume(history),
                    chartPoints = chartPoints(history)
                )
            }
        }
    }

    fun onTabSelected(tab: ExerciseDetailTab) {
        _state.value = _state.value.copy(selectedTab = tab)
    }

    private fun loggedSets(history: List<WorkoutSetEntity>) =
        history.filter { it.weight != null && it.reps != null && it.weight!! > 0 && it.reps!! > 0 }

    private fun estimate1RM(history: List<WorkoutSetEntity>): Double? =
        loggedSets(history).maxOfOrNull { it.weight!! * (1 + it.reps!! / 30.0) }

    private fun bestSetVolumeLabel(history: List<WorkoutSetEntity>): String? =
        loggedSets(history).maxByOrNull { it.weight!! * it.reps!! }
            ?.let { "${it.weight}kg x ${it.reps}" }

    private fun bestSessionVolume(history: List<WorkoutSetEntity>): Double? =
        loggedSets(history)
            .groupBy { it.workoutId }
            .mapValues { (_, sets) -> sets.sumOf { it.weight!! * it.reps!! } }
            .values
            .maxOrNull()

    private fun chartPoints(history: List<WorkoutSetEntity>): List<ChartPoint> =
        loggedSets(history)
            .groupBy { it.workoutId }
            .map { (_, sets) -> ChartPoint(date = sets.minOf { it.createdAt }, weight = sets.maxOf { it.weight!! }) }
            .sortedBy { it.date }
}
