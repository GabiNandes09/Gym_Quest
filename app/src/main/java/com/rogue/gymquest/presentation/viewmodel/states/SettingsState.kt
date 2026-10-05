package com.rogue.gymquest.presentation.viewmodel.states

import android.net.Uri
import com.rogue.gymquest.data.local.entity.AppTheme
import com.rogue.gymquest.data.local.entity.WeightUnit
import com.rogue.gymquest.domain.model.ImportResult

enum class PendingImportType { JSON, HEVY_CSV }

data class SettingsState(
    val theme: AppTheme = AppTheme.DARK,
    val weightUnit: WeightUnit = WeightUnit.KG,

    val isExporting: Boolean = false,
    val exportedFileUri: Uri? = null,
    val exportErrorMessage: String? = null,

    val isImporting: Boolean = false,
    val showImportConfirm: Boolean = false,
    val pendingImportUri: Uri? = null,
    val pendingImportType: PendingImportType? = null,
    val importResult: ImportResult? = null,
    val importErrorMessage: String? = null
)
