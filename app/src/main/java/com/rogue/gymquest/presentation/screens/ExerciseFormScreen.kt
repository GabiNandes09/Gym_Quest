package com.rogue.gymquest.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rogue.gymquest.data.local.entity.ExerciseType
import com.rogue.gymquest.presentation.viewmodel.ExerciseFormViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private fun exerciseTypeLabel(type: ExerciseType): String = when (type) {
    ExerciseType.WEIGHT_REPS -> "Peso + repetições"
    ExerciseType.BODYWEIGHT_REPS -> "Repetições (sem carga)"
    ExerciseType.TIME -> "Duração"
    ExerciseType.DISTANCE_TIME -> "Distância + tempo"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseFormScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    viewModel: ExerciseFormViewModel = koinViewModel(parameters = { parametersOf(exerciseId) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Editar exercício" else "Novo exercício") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Nome do exercício") },
                modifier = Modifier.fillMaxWidth()
            )

            var muscleGroupExpanded by remember { mutableStateOf(false) }
            val selectedMuscleGroupName = state.muscleGroups
                .firstOrNull { it.id == state.selectedMuscleGroupId }?.name ?: ""

            ExposedDropdownMenuBox(
                expanded = muscleGroupExpanded,
                onExpandedChange = { muscleGroupExpanded = it },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                OutlinedTextField(
                    value = selectedMuscleGroupName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Grupo muscular") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = muscleGroupExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = muscleGroupExpanded,
                    onDismissRequest = { muscleGroupExpanded = false }
                ) {
                    state.muscleGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                viewModel.onMuscleGroupSelected(group.id)
                                muscleGroupExpanded = false
                            }
                        )
                    }
                }
            }

            var typeExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                OutlinedTextField(
                    value = exerciseTypeLabel(state.exerciseType),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de exercício") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    ExerciseType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(exerciseTypeLabel(type)) },
                            onClick = {
                                viewModel.onExerciseTypeSelected(type)
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.equipment,
                onValueChange = viewModel::onEquipmentChanged,
                label = { Text("Equipamento (opcional)") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChanged,
                label = { Text("Observações (opcional)") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            state.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = viewModel::onSave,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text("Salvar")
            }
        }
    }
}
