package com.rogue.gymquest.di

import com.rogue.gymquest.presentation.viewmodel.ExerciseDetailViewModel
import com.rogue.gymquest.presentation.viewmodel.ExerciseFormViewModel
import com.rogue.gymquest.presentation.viewmodel.ExerciseListViewModel
import com.rogue.gymquest.presentation.viewmodel.HistoryViewModel
import com.rogue.gymquest.presentation.viewmodel.HomeViewModel
import com.rogue.gymquest.presentation.viewmodel.SettingsViewModel
import com.rogue.gymquest.presentation.viewmodel.ThemeViewModel
import com.rogue.gymquest.presentation.viewmodel.WorkoutDetailViewModel
import com.rogue.gymquest.presentation.viewmodel.WorkoutsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ThemeViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { WorkoutsViewModel(get()) }
    viewModel { ExerciseListViewModel(get()) }
    viewModel { (exerciseId: Long) -> ExerciseFormViewModel(get(), get(), exerciseId) }
    viewModel { (exerciseId: Long) -> ExerciseDetailViewModel(get(), get(), get(), exerciseId) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { (workoutId: Long) -> WorkoutDetailViewModel(get(), get(), workoutId) }
}
