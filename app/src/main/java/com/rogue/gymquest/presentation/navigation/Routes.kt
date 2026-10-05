package com.rogue.gymquest.presentation.navigation

sealed class Routes(val route: String) {

    data object Home : Routes("home")

    data object Workouts : Routes("workouts")

    data object Profile : Routes("profile")

    data object ExerciseList : Routes("exercise_list")

    data object ExerciseForm : Routes("exercise_form?exerciseId={exerciseId}") {
        fun create(exerciseId: Long = 0L) = "exercise_form?exerciseId=$exerciseId"
    }

    data object ExerciseDetail : Routes("exercise_detail/{exerciseId}") {
        fun create(exerciseId: Long) = "exercise_detail/$exerciseId"
    }

    data object WorkoutExecution : Routes("workout_execution")

    data object History : Routes("history")

    data object WorkoutDetail : Routes("workout_detail/{workoutId}") {
        fun create(workoutId: Long) = "workout_detail/$workoutId"
    }

    data object Statistics : Routes("statistics")

    data object BodyWeight : Routes("body_weight")

    data object Settings : Routes("settings")
}

val bottomBarRoutes = setOf(
    Routes.Workouts.route,
    Routes.Home.route,
    Routes.Profile.route
)
