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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity
import com.rogue.gymquest.presentation.components.WeightTrendChart
import com.rogue.gymquest.presentation.viewmodel.ExerciseDetailViewModel
import com.rogue.gymquest.presentation.viewmodel.states.ExerciseDetailState
import com.rogue.gymquest.presentation.viewmodel.states.ExerciseDetailTab
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
    onEditClick: () -> Unit,
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
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar exercício")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            val tabs = listOf(ExerciseDetailTab.SUMMARY to "Resumo", ExerciseDetailTab.HISTORY to "Histórico")
            TabRow(selectedTabIndex = tabs.indexOfFirst { it.first == state.selectedTab }) {
                tabs.forEach { (tab, label) ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.onTabSelected(tab) },
                        text = { Text(label) }
                    )
                }
            }

            when (state.selectedTab) {
                ExerciseDetailTab.SUMMARY -> SummaryTab(state = state)
                ExerciseDetailTab.HISTORY -> HistoryTab(history = state.history)
            }
        }
    }
}

@Composable
private fun SummaryTab(state: ExerciseDetailState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(state.exerciseName, style = MaterialTheme.typography.titleLarge)
            Text(
                "Primário: ${state.muscleGroupName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            state.equipment?.let {
                Text("Equipamento: $it", style = MaterialTheme.typography.bodySmall)
            }
            state.notes?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }

        state.maxWeight?.let {
            Text(
                "$it kg",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        WeightTrendChart(
            points = state.chartPoints,
            modifier = Modifier.padding(16.dp)
        )

        HorizontalDivider()

        Text(
            "Recordes pessoais",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        RecordRow("Maior peso", state.maxWeight?.let { "${it}kg" } ?: "-")
        RecordRow("Melhor 1RM estimado", state.bestEstimated1RM?.let { "%.2fkg".format(it) } ?: "-")
        RecordRow("Melhor volume de série", state.bestSetVolumeLabel ?: "-")
        RecordRow("Melhor volume de sessão", state.bestSessionVolume?.let { "${it.toInt()}kg" } ?: "-")
    }
}

@Composable
private fun RecordRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Text(value, color = MaterialTheme.colorScheme.primary)
    }
    HorizontalDivider()
}

@Composable
private fun HistoryTab(history: List<WorkoutSetEntity>) {
    if (history.isEmpty()) {
        Text(
            text = "Nenhuma série registrada ainda.",
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn {
            items(history, key = { it.id }) { set ->
                HistoryRow(set)
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
