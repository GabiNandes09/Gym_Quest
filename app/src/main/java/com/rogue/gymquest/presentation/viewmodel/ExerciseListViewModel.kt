package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.local.entity.ExerciseWithMuscleGroup
import com.rogue.gymquest.data.repository.ExerciseRepository
import com.rogue.gymquest.presentation.viewmodel.states.ExerciseListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseListViewModel(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private var original: List<ExerciseWithMuscleGroup> = emptyList()

    private val _state = MutableStateFlow(ExerciseListState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            exerciseRepository.getAll().collect { exercises ->
                original = exercises
                applyFilter()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilter()
    }

    fun onDeleteExercise(id: Long) {
        viewModelScope.launch {
            if (exerciseRepository.isInUse(id)) {
                _state.value = _state.value.copy(
                    errorMessage = "Este exercício já tem séries registradas e não pode ser excluído."
                )
            } else {
                exerciseRepository.delete(id)
            }
        }
    }

    fun onErrorShown() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    private fun applyFilter() {
        val query = _state.value.searchQuery
        val filtered = if (query.isBlank()) {
            original
        } else {
            original.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.muscleGroupName.contains(query, ignoreCase = true)
            }
        }
        _state.value = _state.value.copy(exercises = filtered, isLoading = false)
    }
}
