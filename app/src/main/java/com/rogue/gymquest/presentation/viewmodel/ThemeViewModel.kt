package com.rogue.gymquest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogue.gymquest.data.local.entity.AppTheme
import com.rogue.gymquest.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val theme = settingsRepository.theme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppTheme.DARK
    )
}
