package com.ju.passgen.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ju.passgen.ui.screens.GeneratorScreen
import com.ju.passgen.ui.screens.HistoryScreen
import com.ju.passgen.ui.theme.JUPassGenTheme
import com.ju.passgen.viewmodel.PasswordViewModel

object Routes {
    const val GENERATOR = "generator"
    const val HISTORY   = "history"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val vm: PasswordViewModel = viewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    JUPassGenTheme(darkTheme = state.isDark) {
        NavHost(
            navController    = navController,
            startDestination = Routes.GENERATOR,
        ) {
            composable(Routes.GENERATOR) {
                GeneratorScreen(
                    onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                    vm                  = vm,
                )
            }
            composable(Routes.HISTORY) {
                HistoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    vm             = vm,
                )
            }
        }
    }
}
