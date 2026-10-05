package com.rogue.gymquest.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rogue.gymquest.presentation.components.ActiveWorkoutBanner
import com.rogue.gymquest.presentation.components.GymQuestBottomBar
import com.rogue.gymquest.presentation.screens.ExerciseDetailScreen
import com.rogue.gymquest.presentation.screens.ExerciseFormScreen
import com.rogue.gymquest.presentation.screens.ExerciseListScreen
import com.rogue.gymquest.presentation.screens.HistoryScreen
import com.rogue.gymquest.presentation.screens.HomeScreen
import com.rogue.gymquest.presentation.screens.PlaceholderScreen
import com.rogue.gymquest.presentation.screens.ProfileScreen
import com.rogue.gymquest.presentation.screens.SettingsScreen
import com.rogue.gymquest.presentation.screens.WorkoutDetailScreen
import com.rogue.gymquest.presentation.screens.WorkoutsScreen
import com.rogue.gymquest.presentation.viewmodel.ActiveWorkoutViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val showBottomBar = currentRoute?.destination?.route in bottomBarRoutes

    val activeWorkoutViewModel: ActiveWorkoutViewModel = koinViewModel()
    val activeWorkout by activeWorkoutViewModel.inProgressWorkout.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            activeWorkout?.let { workout ->
                ActiveWorkoutBanner(
                    workout = workout,
                    onClick = {
                        navController.navigate(Routes.WorkoutDetail.create(workout.id)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                GymQuestBottomBar(
                    currentRoute = currentRoute?.destination?.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Routes.Home.route)
                            launchSingleTop = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.WorkoutExecution.route) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Iniciar treino")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {

            composable(Routes.Home.route) {
                HomeScreen()
            }

            composable(Routes.Workouts.route) {
                WorkoutsScreen(
                    onViewWorkout = { id -> navController.navigate(Routes.WorkoutDetail.create(id)) },
                    onWorkoutStarted = { id -> navController.navigate(Routes.WorkoutDetail.create(id)) }
                )
            }

            composable(Routes.Profile.route) {
                ProfileScreen(
                    onExercisesClick = { navController.navigate(Routes.ExerciseList.route) },
                    onHistoryClick = { navController.navigate(Routes.History.route) },
                    onSettingsClick = { navController.navigate(Routes.Settings.route) }
                )
            }

            composable(Routes.ExerciseList.route) {
                ExerciseListScreen(
                    onBack = { navController.popBackStack() },
                    onAddExercise = { navController.navigate(Routes.ExerciseForm.create()) },
                    onExerciseClick = { id -> navController.navigate(Routes.ExerciseDetail.create(id)) }
                )
            }

            composable(
                route = Routes.ExerciseForm.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.LongType; defaultValue = 0L })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
                ExerciseFormScreen(
                    exerciseId = exerciseId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.ExerciseDetail.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
                ExerciseDetailScreen(
                    exerciseId = exerciseId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.WorkoutExecution.route) {
                PlaceholderScreen(title = "Execução de treino")
            }

            composable(Routes.History.route) {
                HistoryScreen(
                    onBack = { navController.popBackStack() },
                    onWorkoutClick = { id -> navController.navigate(Routes.WorkoutDetail.create(id)) }
                )
            }

            composable(
                route = Routes.WorkoutDetail.route,
                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
                WorkoutDetailScreen(
                    workoutId = workoutId,
                    onBack = { navController.popBackStack() },
                    onWorkoutStarted = { newId ->
                        navController.popBackStack()
                        navController.navigate(Routes.WorkoutDetail.create(newId))
                    }
                )
            }

            composable(Routes.Statistics.route) {
                PlaceholderScreen(title = "Estatísticas")
            }

            composable(Routes.BodyWeight.route) {
                PlaceholderScreen(title = "Peso corporal")
            }

            composable(Routes.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
