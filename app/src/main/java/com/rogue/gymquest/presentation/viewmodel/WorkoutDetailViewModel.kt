package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.local.entity.WorkoutSetWithExercise
import com.rogue.gymquest.data.repository.WorkoutRepository
import com.rogue.gymquest.data.repository.WorkoutSetRepository
import com.rogue.gymquest.presentation.viewmodel.states.SetGroup
import com.rogue.gymquest.presentation.viewmodel.states.WorkoutDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutDetailViewModel(
    workoutRepository: WorkoutRepository,
    workoutSetRepository: WorkoutSetRepository,
    workoutId: Long
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
