package com.rogue.gymquest.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rogue.gymquest.data.local.entity.AppTheme
import com.rogue.gymquest.data.local.entity.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {

    private val themeKey = stringPreferencesKey("theme")
    private val weightUnitKey = stringPreferencesKey("weight_unit")
    private val languageKey = stringPreferencesKey("language")

    val theme: Flow<AppTheme> = dataStore.data.map { prefs ->
        prefs[themeKey]?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() } ?: AppTheme.DARK
    }

    val weightUnit: Flow<WeightUnit> = dataStore.data.map { prefs ->
        prefs[weightUnitKey]?.let { runCatching { WeightUnit.valueOf(it) }.getOrNull() } ?: WeightUnit.KG
    }

    val language: Flow<String> = dataStore.data.map { prefs ->
        prefs[languageKey] ?: "pt"
    }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[themeKey] = theme.name }
    }

    suspend fun setWeightUnit(weightUnit: WeightUnit) {
        dataStore.edit { it[weightUnitKey] = weightUnit.name }
    }

    suspend fun setLanguage(languageCode: String) {
        dataStore.edit { it[languageKey] = languageCode }
    }
}
