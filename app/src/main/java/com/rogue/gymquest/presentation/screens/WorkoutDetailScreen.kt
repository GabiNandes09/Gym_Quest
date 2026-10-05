package com.rogue.gymquest.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.rogue.gymquest.data.local.entity.SetType
import com.rogue.gymquest.data.local.entity.WorkoutSetWithExercise
import com.rogue.gymquest.data.local.entity.WorkoutStatus
import com.rogue.gymquest.presentation.components.ConfirmDialog
import com.rogue.gymquest.presentation.viewmodel.WorkoutDetailViewModel
import com.rogue.gymquest.presentation.viewmodel.states.SetGroup
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun setTypeLabel(type: SetType): String = when (type) {
    SetType.WARMUP -> "Aquecimento"
    SetType.NORMAL -> "Normal"
    SetType.FAILURE -> "Falha"
    SetType.DROP_SET -> "Drop set"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: Long,
    onBack: () -> Unit,
    onWorkoutStarted: (Long) -> Unit,
    viewModel: WorkoutDetailViewModel = koinViewModel(parameters = { parametersOf(workoutId) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val formatter = remember { SimpleDateFormat("EEE, dd/MM/yyyy HH:mm", Locale("pt", "BR")) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showCancelConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(state.startedWorkoutId) {
        state.startedWorkoutId?.let {
            onWorkoutStarted(it)
            viewModel.onStartedWorkoutHandled()
        }
    }

    LaunchedEffect(state.shouldNavigateBack) {
        if (state.shouldNavigateBack) onBack()
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.workout?.name ?: "Treino") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (state.workout?.status == WorkoutStatus.COMPLETED) {
                        IconButton(onClick = viewModel::onPlayClick, enabled = !state.isProcessing) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Iniciar treino")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            state.workout?.let { workout ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(formatter.format(Date(workout.date)), style = MaterialTheme.typography.bodyMedium)
                    val durationMinutes = workout.finishedAt?.let { (it - workout.startedAt) / 60000 }
                    if (durationMinutes != null) {
                        Text(
                            "Duração: $durationMinutes min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    workout.notes?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    }

                    if (workout.status == WorkoutStatus.IN_PROGRESS) {
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            OutlinedButton(
                                onClick = { showCancelConfirm = true },
                                enabled = !state.isProcessing,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                            OutlinedButton(
                                onClick = onBack,
                                enabled = !state.isProcessing,
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                            ) {
                                Text("Pausar")
                            }
                            Button(
                                onClick = viewModel::onFinishClick,
                                enabled = !state.isProcessing,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Finalizar")
                            }
                        }
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.setGroups, key = { it.exerciseId.toString() + it.sets.first().id }) { group ->
                    ExerciseGroupCard(group)
                }
            }
        }
    }

    if (showCancelConfirm) {
        ConfirmDialog(
            title = "Cancelar treino",
            message = "Isso vai descartar este treino e todas as séries registradas nele. Essa ação não pode ser desfeita.",
            confirmLabel = "Cancelar treino",
            onConfirm = {
                showCancelConfirm = false
                viewModel.onCancelClick()
            },
            onDismiss = { showCancelConfirm = false }
        )
    }
}

@Composable
private fun ExerciseGroupCard(group: SetGroup) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(group.exerciseName, style = MaterialTheme.typography.titleMedium)
            group.sets.forEachIndexed { index, set ->
                SetRow(index + 1, set)
            }
        }
    }
}

@Composable
private fun SetRow(index: Int, set: WorkoutSetWithExercise) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Text("$index.", modifier = Modifier.padding(end = 8.dp))
        val description = buildList {
            set.weight?.let { add("${it} kg") }
            set.reps?.let { add("${it} reps") }
            set.durationSeconds?.let { add("${it}s") }
            set.distanceMeters?.let { add("${it} m") }
        }.joinToString(" x ")
        Column {
            Text(description)
            if (set.setType != SetType.NORMAL) {
                Text(
                    setTypeLabel(set.setType),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            set.notes?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
