package com.rogue.gymquest.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity
import com.rogue.gymquest.presentation.viewmodel.ExerciseDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    viewModel: ExerciseDetailViewModel = koinViewModel(parameters = { parametersOf(exerciseId) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.exerciseName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Grupo muscular: ${state.muscleGroupName}")
                    state.equipment?.let { Text("Equipamento: $it") }
                    state.notes?.let { Text("Observações: $it") }
                    Text(
                        text = "PR: ${state.personalRecordWeight?.let { "$it kg" } ?: "sem registro"}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Text(
                text = "Histórico",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (state.history.isEmpty()) {
                Text(
                    text = "Nenhuma série registrada ainda.",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                LazyColumn {
                    items(state.history, key = { it.id }) { set ->
                        HistoryRow(set)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(set: WorkoutSetEntity) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")) }
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(formatter.format(Date(set.createdAt)), style = MaterialTheme.typography.bodySmall)
            val description = buildList {
                set.weight?.let { add("${it} kg") }
                set.reps?.let { add("${it} reps") }
                set.durationSeconds?.let { add("${it}s") }
                set.distanceMeters?.let { add("${it} m") }
            }.joinToString(" x ")
            Text(description)
        }
    }
}
