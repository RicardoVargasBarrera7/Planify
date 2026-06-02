package com.example.planify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.planify.ui.LoginScreen
import com.example.planify.ui.ProfileScreen
import com.example.planify.ui.TasksScreen
import com.example.planify.viewmodel.LoginViewModel
import com.example.planify.viewmodel.TaskViewModel

/**
 * Grafo de navegación principal de Planify.
 * Recibe los ViewModels ya instanciados desde MainActivity para evitar
 * problemas de compilación con LocalContext dentro de NavHost.
 */
@Composable
fun NavGraph(
    navController   : NavHostController,
    loginViewModel  : LoginViewModel,
    taskViewModel   : TaskViewModel
) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {

        // ── Pantalla de Login ──────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel      = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Pantalla de Perfil / Bienvenida ───────────────────────────────
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToTasks = {
                    navController.navigate(Screen.Tasks.route)
                },
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Pantalla de Tareas ─────────────────────────────────────────────
        composable(Screen.Tasks.route) {
            TasksScreen(
                viewModel = taskViewModel,
                onBack    = { navController.popBackStack() }
            )
        }
    }
}
