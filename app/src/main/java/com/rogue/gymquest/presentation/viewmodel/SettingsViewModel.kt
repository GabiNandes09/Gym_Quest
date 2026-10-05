package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.local.entity.AppTheme
import com.rogue.gymquest.data.local.entity.WeightUnit
import com.rogue.gymquest.data.repository.SettingsRepository
import com.rogue.gymquest.presentation.viewmodel.states.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
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
}
