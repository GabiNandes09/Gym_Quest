package com.rogue.gymquest.di

import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val android.content.Context.dataStore by preferencesDataStore(name = "gymquest_settings")

val preferencesModule = module {
    single { androidContext().dataStore }
}
