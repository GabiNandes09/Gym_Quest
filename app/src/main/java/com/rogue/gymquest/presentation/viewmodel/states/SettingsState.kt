package com.rogue.gymquest.presentation.viewmodel.states

import com.rogue.gymquest.data.local.entity.AppTheme
import com.rogue.gymquest.data.local.entity.WeightUnit

data class SettingsState(
    val theme: AppTheme = AppTheme.DARK,
    val weightUnit: WeightUnit = WeightUnit.KG
)
