package com.rogue.gymquest.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rogue.gymquest.data.local.entity.AppTheme
import com.rogue.gymquest.data.local.entity.WeightUnit
import com.rogue.gymquest.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Configurações") }) }
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
        }
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
