package com.example.compose.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.common.ThemeType
import com.example.compose.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val darkTheme = shouldUseDarkTheme(viewModel.state.value)
            AppTheme(useDarkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(navController)
                }
            }
        }
    }

    @Composable
    private fun shouldUseDarkTheme(uiState: MainUiState): Boolean =
        when (uiState.themeType) {
            ThemeType.SYSTEM -> isSystemInDarkTheme()
            ThemeType.LIGHT -> false
            ThemeType.DARK -> true
            else -> isSystemInDarkTheme()
        }
}