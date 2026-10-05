package com.rogue.gymquest.di

import com.rogue.gymquest.data.repository.BackupRepository
import com.rogue.gymquest.data.repository.BodyWeightLogRepository
import com.rogue.gymquest.data.repository.ExerciseRepository
import com.rogue.gymquest.data.repository.MuscleGroupRepository
import com.rogue.gymquest.data.repository.SettingsRepository
import com.rogue.gymquest.data.repository.WorkoutRepository
import com.rogue.gymquest.data.repository.WorkoutSetRepository
import com.rogue.gymquest.domain.usecase.StartWorkoutFromTemplateUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { MuscleGroupRepository(get()) }
    single { ExerciseRepository(get()) }
    single { WorkoutRepository(get()) }
    single { WorkoutSetRepository(get()) }
    single { BodyWeightLogRepository(get()) }
    single { SettingsRepository(get()) }
    single { BackupRepository(androidContext(), get(), get(), get(), get(), get(), get()) }
    single { StartWorkoutFromTemplateUseCase(get(), get()) }
}
