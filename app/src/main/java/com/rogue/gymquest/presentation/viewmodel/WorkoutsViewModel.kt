package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.repository.WorkoutRepository
import com.rogue.gymquest.data.repository.WorkoutSetRepository
import com.rogue.gymquest.domain.usecase.StartWorkoutFromTemplateUseCase
import com.rogue.gymquest.presentation.viewmodel.states.WorkoutsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkoutsViewModel(
    private val workoutRepository: WorkoutRepository,
    private val workoutSetRepository: WorkoutSetRepository,
    private val startWorkoutFromTemplate: StartWorkoutFromTemplateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepository.getInProgressFlow().collect { inProgress ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    hasInProgressWorkout = inProgress != null,
                    inProgressWorkoutId = inProgress?.id
                )
            }
        }
        viewModelScope.launch {
            workoutRepository.getRoutineTemplates().collect { templates ->
                _state.value = _state.value.copy(routineTemplates = templates)

                val previews = templates.associate { template ->
                    val sets = workoutSetRepository.getByWorkoutIdWithExercise(template.id).first()
                    template.id to sets.map { it.exerciseName }.distinct().joinToString(", ")
                }
                _state.value = _state.value.copy(exercisePreviewByTemplateId = previews)
            }
        }
    }

    fun onStartRoutineClick(templateId: Long) {
        if (_state.value.hasInProgressWorkout || _state.value.isStarting) return
        val template = _state.value.routineTemplates.firstOrNull { it.id == templateId } ?: return

        _state.value = _state.value.copy(isStarting = true)

        viewModelScope.launch {
            if (startWorkoutFromTemplate.hasInProgressWorkout()) {
                _state.value = _state.value.copy(
                    isStarting = false,
                    errorMessage = "Você já tem um treino em andamento."
                )
                return@launch
            }
            val newWorkoutId = startWorkoutFromTemplate.start(template)
            _state.value = _state.value.copy(isStarting = false, startedWorkoutId = newWorkoutId)
        }
    }

    fun onStartedWorkoutHandled() {
        _state.value = _state.value.copy(startedWorkoutId = null)
    }

    fun onErrorShown() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
