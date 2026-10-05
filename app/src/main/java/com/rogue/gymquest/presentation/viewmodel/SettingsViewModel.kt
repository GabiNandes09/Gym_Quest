package com.rogue.gymquest.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.local.entity.AppTheme
import com.rogue.gymquest.data.local.entity.WeightUnit
import com.rogue.gymquest.data.repository.BackupRepository
import com.rogue.gymquest.data.repository.SettingsRepository
import com.rogue.gymquest.presentation.viewmodel.states.PendingImportType
import com.rogue.gymquest.presentation.viewmodel.states.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.theme.collect { theme ->
                _state.value = _state.value.copy(theme = theme)
            }
        }
        viewModelScope.launch {
            settingsRepository.weightUnit.collect { unit ->
                _state.value = _state.value.copy(weightUnit = unit)
            }
        }
    }

    fun onThemeChanged(theme: AppTheme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    fun onWeightUnitChanged(unit: WeightUnit) {
        viewModelScope.launch { settingsRepository.setWeightUnit(unit) }
    }

    fun onExportClick() {
        if (_state.value.isExporting) return

        _state.value = _state.value.copy(isExporting = true)

        viewModelScope.launch {
            try {
                val uri = backupRepository.exportBackup()
                _state.value = _state.value.copy(isExporting = false, exportedFileUri = uri)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isExporting = false,
                    exportErrorMessage = "Não foi possível exportar os dados."
                )
            }
        }
    }

    fun onExportHandled() {
        _state.value = _state.value.copy(exportedFileUri = null)
    }

    fun onExportErrorShown() {
        _state.value = _state.value.copy(exportErrorMessage = null)
    }

    fun onImportJsonFileSelected(uri: Uri) {
        _state.value = _state.value.copy(
            showImportConfirm = true,
            pendingImportUri = uri,
            pendingImportType = PendingImportType.JSON
        )
    }

    fun onImportHevyCsvFileSelected(uri: Uri) {
        _state.value = _state.value.copy(
            showImportConfirm = true,
            pendingImportUri = uri,
            pendingImportType = PendingImportType.HEVY_CSV
        )
    }

    fun onDismissImportConfirm() {
        _state.value = _state.value.copy(
            showImportConfirm = false,
            pendingImportUri = null,
            pendingImportType = null
        )
    }

    fun onConfirmImport() {
        val uri = _state.value.pendingImportUri ?: return
        val type = _state.value.pendingImportType ?: return

        _state.value = _state.value.copy(showImportConfirm = false, isImporting = true)

        viewModelScope.launch {
            try {
                val result = when (type) {
                    PendingImportType.JSON -> backupRepository.importBackup(uri)
                    PendingImportType.HEVY_CSV -> backupRepository.importHevyCsv(uri)
                }
                _state.value = _state.value.copy(
                    isImporting = false,
                    pendingImportUri = null,
                    pendingImportType = null,
                    importResult = result
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isImporting = false,
                    pendingImportUri = null,
                    pendingImportType = null,
                    importErrorMessage = "Não foi possível importar o arquivo."
                )
            }
        }
    }

    fun onImportResultShown() {
        _state.value = _state.value.copy(importResult = null)
    }

    fun onImportErrorShown() {
        _state.value = _state.value.copy(importErrorMessage = null)
    }
}
