package com.rogue.gymquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rogue.gymquest.data.local.entity.AppTheme
import com.rogue.gymquest.presentation.navigation.AppNavigation
import com.rogue.gymquest.presentation.viewmodel.ThemeViewModel
import com.rogue.gymquest.ui.theme.GymQuestTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel()
            val theme by themeViewModel.theme.collectAsStateWithLifecycle()

            GymQuestTheme(darkTheme = theme == AppTheme.DARK) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
