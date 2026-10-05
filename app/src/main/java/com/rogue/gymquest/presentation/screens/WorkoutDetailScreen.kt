package com.rogue.gymquest.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rogue.gymquest.data.local.entity.SetType
import com.rogue.gymquest.data.local.entity.WorkoutSetWithExercise
import com.rogue.gymquest.data.local.entity.WorkoutStatus
import com.rogue.gymquest.presentation.components.ConfirmDialog
import com.rogue.gymquest.presentation.viewmodel.WorkoutDetailViewModel
import com.rogue.gymquest.presentation.viewmodel.states.SetGroup
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun setTypeColor(type: SetType): Color = when (type) {
    SetType.WARMUP -> Color(0xFFD4AF37)
    SetType.NORMAL -> Color(0xFF3A3A3A)
    SetType.FAILURE -> Color(0xFFE53935)
    SetType.DROP_SET -> Color(0xFF8E24AA)
}

private fun setBadgeLabel(type: SetType, workingIndex: Int): String =
    if (type == SetType.WARMUP) "W" else workingIndex.toString()

private fun restLabel(seconds: Int): String {
    if (seconds <= 0) return "Sem descanso"
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes > 0) {
        if (remainingSeconds > 0) "Descanso: ${minutes}min ${remainingSeconds}s" else "Descanso: ${minutes}min"
    } else {
        "Descanso: ${remainingSeconds}s"
    }
}

private fun durationLabel(seconds: Long): String = when {
    seconds < 60 -> "${seconds}s"
    seconds < 3600 -> "${seconds / 60}min"
    else -> "${seconds / 3600}h ${(seconds % 3600) / 60}min"
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
    val dateFormatter = remember { SimpleDateFormat("EEE, dd/MM/yyyy HH:mm", Locale("pt", "BR")) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showCancelConfirm by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

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

    val workout = state.workout
    val inProgress = workout?.status == WorkoutStatus.IN_PROGRESS

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name ?: "Treino") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (workout?.status == WorkoutStatus.COMPLETED) {
                        IconButton(onClick = viewModel::onPlayClick, enabled = !state.isProcessing) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Iniciar treino")
                        }
                    }
                    if (inProgress) {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Mais opções")
                        }
                        DropdownMenu(expanded = showOverflowMenu, onDismissRequest = { showOverflowMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Cancelar treino") },
                                onClick = {
                                    showOverflowMenu = false
                                    showCancelConfirm = true
                                }
                            )
                        }
                        TextButton(onClick = viewModel::onFinishClick, enabled = !state.isProcessing) {
                            Text("Concluir")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            workout?.let {
                if (inProgress) {
                    LiveStatsRow(startedAt = workout.startedAt, setGroups = state.setGroups)
                } else {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(dateFormatter.format(Date(workout.date)), style = MaterialTheme.typography.bodyMedium)
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
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.setGroups, key = { it.exerciseId.toString() + it.sets.first().id }) { group ->
                    ExerciseGroupCard(
                        group = group,
                        editable = inProgress,
                        onWeightChanged = viewModel::onSetWeightChanged,
                        onRepsChanged = viewModel::onSetRepsChanged,
                        onCompletedToggled = viewModel::onSetCompletedToggled,
                        onAddSet = { viewModel.onAddSetClick(group) }
                    )
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
private fun LiveStatsRow(startedAt: Long, setGroups: List<SetGroup>) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(startedAt) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }
    val elapsedSeconds = ((now - startedAt) / 1000).coerceAtLeast(0)

    val completedSets = setGroups.flatMap { it.sets }.filter { it.completed }
    val volume = completedSets.sumOf { (it.weight ?: 0.0) * (it.reps ?: 0) }
    val seriesCount = completedSets.size

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        StatColumn("Duração", durationLabel(elapsedSeconds))
        StatColumn("Volume", "${volume.toInt()} kg")
        StatColumn("Séries", seriesCount.toString())
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(modifier = Modifier.padding(end = 24.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ExerciseGroupCard(
    group: SetGroup,
    editable: Boolean,
    onWeightChanged: (Long, Double?) -> Unit,
    onRepsChanged: (Long, Int?) -> Unit,
    onCompletedToggled: (Long, Boolean) -> Unit,
    onAddSet: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        group.exerciseName.take(1).uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    group.exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            Text(
                restLabel(group.sets.first().restTimeSeconds),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 44.dp, top = 2.dp)
            )

            SetTableHeader(editable)

            var workingIndex = 0
            group.sets.forEach { set ->
                if (set.setType != SetType.WARMUP) workingIndex++
                SetRow(
                    badgeLabel = setBadgeLabel(set.setType, workingIndex),
                    set = set,
                    editable = editable,
                    onWeightChanged = { onWeightChanged(set.id, it) },
                    onRepsChanged = { onRepsChanged(set.id, it) },
                    onCompletedToggled = { onCompletedToggled(set.id, it) }
                )
            }

            if (editable) {
                TextButton(onClick = onAddSet, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("+ Adicionar série")
                }
            }
        }
    }
}

@Composable
private fun SetTableHeader(editable: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp)) {
        HeaderCell("SÉRIE", Modifier.width(40.dp))
        if (editable) {
            HeaderCell("ANTERIOR", Modifier.weight(1f))
        }
        HeaderCell("KG", Modifier.weight(1f))
        HeaderCell("REPS", Modifier.weight(1f))
        if (editable) {
            HeaderCell("", Modifier.width(40.dp))
        }
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier) {
    Text(
        text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SetRow(
    badgeLabel: String,
    set: WorkoutSetWithExercise,
    editable: Boolean,
    onWeightChanged: (Double?) -> Unit,
    onRepsChanged: (Int?) -> Unit,
    onCompletedToggled: (Boolean) -> Unit
) {
    val previousWeight = remember(set.id) { set.weight }
    val previousReps = remember(set.id) { set.reps }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(40.dp).padding(end = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(setTypeColor(set.setType), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    badgeLabel,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (editable) {
            val previousText = buildList {
                previousWeight?.let { add("${it}kg") }
                previousReps?.let { add("x $it") }
            }.joinToString(" ")
            Text(
                previousText.ifBlank { "-" },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            var weightText by remember(set.id) { mutableStateOf(set.weight?.toString() ?: "") }
            var repsText by remember(set.id) { mutableStateOf(set.reps?.toString() ?: "") }

            OutlinedTextField(
                value = weightText,
                onValueChange = {
                    weightText = it
                    onWeightChanged(it.toDoubleOrNull())
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )
            OutlinedTextField(
                value = repsText,
                onValueChange = {
                    repsText = it
                    onRepsChanged(it.toIntOrNull())
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )
            Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.Center) {
                Checkbox(checked = set.completed, onCheckedChange = onCompletedToggled)
            }
        } else if (set.durationSeconds != null || set.distanceMeters != null) {
            val description = buildList {
                set.distanceMeters?.let { add("${it} m") }
                set.durationSeconds?.let { add("${it}s") }
            }.joinToString(" · ")
            Text(description, modifier = Modifier.weight(2f))
            set.notes?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            }
        } else {
            Text(set.weight?.toString() ?: "-", modifier = Modifier.weight(1f))
            Text(set.reps?.toString() ?: "-", modifier = Modifier.weight(1f))
            set.notes?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            }
        }
    }
}
