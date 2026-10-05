package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.repository.ExerciseRepository
import com.rogue.gymquest.data.repository.MuscleGroupRepository
import com.rogue.gymquest.data.repository.WorkoutSetRepository
import com.rogue.gymquest.presentation.viewmodel.states.ExerciseDetailState
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
            workoutSetRepository.getPersonalRecord(exerciseId).collect { pr ->
                _state.value = _state.value.copy(personalRecordWeight = pr?.weight)
            }
        }

        viewModelScope.launch {
            workoutSetRepository.getByExerciseId(exerciseId).collect { history ->
                _state.value = _state.value.copy(history = history)
            }
        }
    }
}
