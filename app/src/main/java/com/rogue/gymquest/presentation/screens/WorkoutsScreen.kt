package com.rogue.gymquest.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rogue.gymquest.data.local.entity.WorkoutEntity
import com.rogue.gymquest.presentation.viewmodel.WorkoutsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    onViewWorkout: (Long) -> Unit,
    viewModel: WorkoutsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Treinos") })
        }
    ) { padding ->
        if (state.isLoading) {
            // nothing to show yet
        } else if (state.hasInProgressWorkout) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)
            ) {
                Text(
                    text = "Você tem um treino em andamento.",
                    style = MaterialTheme.typography.titleMedium
                )
                Card(
                    onClick = { state.inProgressWorkoutId?.let(onViewWorkout) },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(
                        "Ver treino em andamento",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else if (state.routineTemplates.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nenhum treino cadastrado ainda.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Toque no botão dourado para começar um novo treino.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    "Treinos cadastrados",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.routineTemplates, key = { it.id }) { template ->
                        RoutineCard(
                            template = template,
                            onClick = { onViewWorkout(template.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineCard(template: WorkoutEntity, onClick: () -> Unit) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(template.name ?: "Treino", style = MaterialTheme.typography.titleMedium)
            Text(
                "Último em ${formatter.format(Date(template.date))} — toque para ver",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
