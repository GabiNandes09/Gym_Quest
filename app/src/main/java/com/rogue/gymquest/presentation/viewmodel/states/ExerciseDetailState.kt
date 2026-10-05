package com.rogue.gymquest.presentation.viewmodel.states

import com.rogue.gymquest.data.local.entity.ExerciseType
import com.rogue.gymquest.data.local.entity.WorkoutSetEntity

enum class ExerciseDetailTab { SUMMARY, HISTORY }

data class ChartPoint(val date: Long, val weight: Double)

data class ExerciseDetailState(
    val isLoading: Boolean = true,
    val exerciseName: String = "",
    val muscleGroupName: String = "",
    val equipment: String? = null,
    val notes: String? = null,
    val exerciseType: ExerciseType = ExerciseType.WEIGHT_REPS,
    val selectedTab: ExerciseDetailTab = ExerciseDetailTab.SUMMARY,
    val maxWeight: Double? = null,
    val bestEstimated1RM: Double? = null,
    val bestSetVolumeLabel: String? = null,
    val bestSessionVolume: Double? = null,
    val chartPoints: List<ChartPoint> = emptyList(),
    val history: List<WorkoutSetEntity> = emptyList()
)
