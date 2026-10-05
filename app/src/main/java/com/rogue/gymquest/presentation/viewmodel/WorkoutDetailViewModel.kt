package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.local.entity.WorkoutSetWithExercise
import com.rogue.gymquest.data.local.entity.WorkoutStatus
import com.rogue.gymquest.data.repository.WorkoutRepository
import com.rogue.gymquest.data.repository.WorkoutSetRepository
import com.rogue.gymquest.presentation.viewmodel.states.SetGroup
import com.rogue.gymquest.presentation.viewmodel.states.WorkoutDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkoutDetailViewModel(
    private val workoutRepository: WorkoutRepository,
    private val workoutSetRepository: WorkoutSetRepository,
    private val workoutId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutDetailState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepository.getById(workoutId).collect { workout ->
                _state.value = _state.value.copy(isLoading = false, workout = workout)
            }
        }
        viewModelScope.launch {
            workoutSetRepository.getByWorkoutIdWithExercise(workoutId).collect { sets ->
                _state.value = _state.value.copy(setGroups = groupConsecutive(sets))
            }
        }
    }

    fun onPlayClick() {
        val workout = _state.value.workout ?: return
        if (workout.status != WorkoutStatus.COMPLETED || _state.value.isProcessing) return

        _state.value = _state.value.copy(isProcessing = true)

        viewModelScope.launch {
            if (workoutRepository.getInProgress() != null) {
                _state.value = _state.value.copy(
                    isProcessing = false,
                    errorMessage = "Você já tem um treino em andamento. Finalize ou cancele antes de iniciar outro."
                )
                return@launch
            }

            val templateSets = workoutSetRepository.getByWorkoutId(workout.id).first()
            val newWorkoutId = workoutRepository.startNew(name = workout.name)

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

            _state.value = _state.value.copy(isProcessing = false, startedWorkoutId = newWorkoutId)
        }
    }

    fun onCancelClick() {
        val workout = _state.value.workout ?: return
        if (_state.value.isProcessing) return

        _state.value = _state.value.copy(isProcessing = true)

        viewModelScope.launch {
            workoutSetRepository.deleteByWorkoutId(workout.id)
            workoutRepository.delete(workout.id)
            _state.value = _state.value.copy(isProcessing = false, shouldNavigateBack = true)
        }
    }

    fun onFinishClick() {
        val workout = _state.value.workout ?: return
        if (_state.value.isProcessing) return

        _state.value = _state.value.copy(isProcessing = true)

        viewModelScope.launch {
            workoutRepository.finish(workout)
            _state.value = _state.value.copy(isProcessing = false, shouldNavigateBack = true)
        }
    }

    fun onStartedWorkoutHandled() {
        _state.value = _state.value.copy(startedWorkoutId = null)
    }

    fun onErrorShown() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    private fun groupConsecutive(sets: List<WorkoutSetWithExercise>): List<SetGroup> {
        val groups = mutableListOf<SetGroup>()
        for (set in sets) {
            val last = groups.lastOrNull()
            if (last != null && last.exerciseId == set.exerciseId) {
                groups[groups.lastIndex] = last.copy(sets = last.sets + set)
            } else {
                groups.add(SetGroup(set.exerciseId, set.exerciseName, listOf(set)))
            }
        }
        return groups
    }
}
