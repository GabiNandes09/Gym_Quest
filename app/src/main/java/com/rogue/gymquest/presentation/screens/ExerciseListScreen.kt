package com.rogue.gymquest.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rogue.gymquest.data.local.entity.ExerciseWithMuscleGroup
import com.rogue.gymquest.presentation.viewmodel.ExerciseListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    onAddExercise: () -> Unit,
    onExerciseClick: (Long) -> Unit,
    viewModel: ExerciseListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exercícios") },
                actions = {
                    IconButton(onClick = onAddExercise) {
                        Icon(Icons.Filled.Add, contentDescription = "Adicionar exercício")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text("Buscar exercício") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            if (state.exercises.isEmpty() && !state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum exercício cadastrado ainda.")
                }
            } else {
                LazyColumn {
                    items(state.exercises, key = { it.id }) { exercise ->
                        ExerciseRow(
                            exercise = exercise,
                            onClick = { onExerciseClick(exercise.id) },
                            onDelete = { viewModel.onDeleteExercise(exercise.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseRow(
    exercise: ExerciseWithMuscleGroup,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    exercise.muscleGroupName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Excluir")
            }
        }
    }
}
