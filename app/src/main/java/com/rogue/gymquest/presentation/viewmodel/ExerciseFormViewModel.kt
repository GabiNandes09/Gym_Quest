package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.local.entity.ExerciseType
import com.rogue.gymquest.data.repository.ExerciseRepository
import com.rogue.gymquest.data.repository.MuscleGroupRepository
import com.rogue.gymquest.presentation.viewmodel.states.ExerciseFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseFormViewModel(
    private val exerciseRepository: ExerciseRepository,
    muscleGroupRepository: MuscleGroupRepository,
    private val exerciseId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseFormState(isEditing = exerciseId != 0L))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            muscleGroupRepository.getAll().collect { groups ->
                _state.value = _state.value.copy(
                    muscleGroups = groups,
                    selectedMuscleGroupId = _state.value.selectedMuscleGroupId ?: groups.firstOrNull()?.id
                )
            }
        }

        if (exerciseId != 0L) {
            viewModelScope.launch {
                exerciseRepository.getById(exerciseId)?.let { exercise ->
                    _state.value = _state.value.copy(
                        name = exercise.name,
                        selectedMuscleGroupId = exercise.muscleGroupId,
                        exerciseType = exercise.exerciseType,
                        equipment = exercise.equipment ?: "",
                        notes = exercise.notes ?: ""
                    )
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        _state.value = _state.value.copy(name = name, errorMessage = null)
    }

    fun onMuscleGroupSelected(id: Long) {
        _state.value = _state.value.copy(selectedMuscleGroupId = id)
    }

    fun onExerciseTypeSelected(type: ExerciseType) {
        _state.value = _state.value.copy(exerciseType = type)
    }

    fun onEquipmentChanged(equipment: String) {
        _state.value = _state.value.copy(equipment = equipment)
    }

    fun onNotesChanged(notes: String) {
        _state.value = _state.value.copy(notes = notes)
    }

    fun onSave() {
        val current = _state.value
        val muscleGroupId = current.selectedMuscleGroupId

        if (current.name.isBlank() || muscleGroupId == null) {
            _state.value = current.copy(errorMessage = "Informe o nome e o grupo muscular.")
            return
        }

        viewModelScope.launch {
            if (current.isEditing) {
                val existing = exerciseRepository.getById(exerciseId)
                if (existing != null) {
                    exerciseRepository.update(
                        existing.copy(
                            name = current.name.trim(),
                            muscleGroupId = muscleGroupId,
                            exerciseType = current.exerciseType,
                            equipment = current.equipment.ifBlank { null },
                            notes = current.notes.ifBlank { null }
                        )
                    )
                }
            } else {
                exerciseRepository.create(
                    name = current.name,
                    muscleGroupId = muscleGroupId,
                    exerciseType = current.exerciseType,
                    equipment = current.equipment.ifBlank { null },
                    notes = current.notes.ifBlank { null }
                )
            }
            _state.value = _state.value.copy(saved = true)
        }
    }
}
