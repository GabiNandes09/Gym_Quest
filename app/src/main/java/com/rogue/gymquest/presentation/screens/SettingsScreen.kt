package com.rogue.gymquest.presentation.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rogue.gymquest.data.local.entity.AppTheme
import com.rogue.gymquest.data.local.entity.WeightUnit
import com.rogue.gymquest.presentation.components.ConfirmDialog
import com.rogue.gymquest.presentation.viewmodel.SettingsViewModel
import com.rogue.gymquest.presentation.viewmodel.states.PendingImportType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val importJsonFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.onImportJsonFileSelected(it) } }

    val importHevyCsvFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.onImportHevyCsvFileSelected(it) } }

    LaunchedEffect(state.importResult) {
        state.importResult?.let { result ->
            Toast.makeText(
                context,
                "Importado: ${result.muscleGroups} grupos musculares, ${result.exercises} exercícios, " +
                    "${result.workouts} treinos, ${result.workoutSets} séries, ${result.bodyWeightLogs} registros de peso.",
                Toast.LENGTH_LONG
            ).show()
            viewModel.onImportResultShown()
        }
    }

    LaunchedEffect(state.importErrorMessage) {
        state.importErrorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onImportErrorShown()
        }
    }

    LaunchedEffect(state.exportedFileUri) {
        state.exportedFileUri?.let { uri ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, null))
            viewModel.onExportHandled()
        }
    }

    LaunchedEffect(state.exportErrorMessage) {
        state.exportErrorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onExportErrorShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(padding).padding(16.dp)) {
            Text("Tema", style = MaterialTheme.typography.titleMedium)
            SettingsRadioRow(
                label = "Escuro",
                selected = state.theme == AppTheme.DARK,
                onClick = { viewModel.onThemeChanged(AppTheme.DARK) }
            )
            SettingsRadioRow(
                label = "Claro",
                selected = state.theme == AppTheme.LIGHT,
                onClick = { viewModel.onThemeChanged(AppTheme.LIGHT) }
            )

            Text(
                "Unidade de peso",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
            SettingsRadioRow(
                label = "Quilogramas (kg)",
                selected = state.weightUnit == WeightUnit.KG,
                onClick = { viewModel.onWeightUnitChanged(WeightUnit.KG) }
            )
            SettingsRadioRow(
                label = "Libras (lb)",
                selected = state.weightUnit == WeightUnit.LB,
                onClick = { viewModel.onWeightUnitChanged(WeightUnit.LB) }
            )

            Text(
                "Dados",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
            Button(
                onClick = viewModel::onExportClick,
                enabled = !state.isExporting,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(if (state.isExporting) "Exportando..." else "Exportar dados (JSON)")
            }
            Button(
                onClick = { importJsonFilePicker.launch(arrayOf("application/json")) },
                enabled = !state.isImporting,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(if (state.isImporting) "Importando..." else "Importar dados (JSON)")
            }
            Button(
                onClick = {
                    importHevyCsvFilePicker.launch(
                        arrayOf("text/comma-separated-values", "text/csv", "text/plain", "application/csv")
                    )
                },
                enabled = !state.isImporting,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(if (state.isImporting) "Importando..." else "Importar treinos do Hevy (CSV)")
            }
        }
    }

    if (state.showImportConfirm) {
        val message = when (state.pendingImportType) {
            PendingImportType.HEVY_CSV -> "Isso vai importar o histórico de treinos do Hevy: exercícios novos " +
                "entram no grupo \"A Classificar\" (ajuste o grupo muscular depois, em cada exercício). " +
                "Treinos e séries são sempre adicionados, nunca substituídos. Continuar?"
            else -> "Isso vai adicionar os dados do arquivo ao que já existe no app (grupos musculares e " +
                "exercícios com o mesmo nome são reaproveitados; treinos e séries são sempre adicionados). Continuar?"
        }
        ConfirmDialog(
            title = "Importar dados",
            message = message,
            confirmLabel = "Importar",
            onConfirm = viewModel::onConfirmImport,
            onDismiss = viewModel::onDismissImportConfirm
        )
    }
}

@Composable
private fun SettingsRadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
    }
}
