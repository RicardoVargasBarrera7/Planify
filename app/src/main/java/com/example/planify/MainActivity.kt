package com.example.planify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.planify.data.TaskRepository
import com.example.planify.navigation.NavGraph
import com.example.planify.ui.theme.PlanifyTheme
import com.example.planify.viewmodel.LoginViewModel
import com.example.planify.viewmodel.TaskViewModel
import com.example.planify.viewmodel.TaskViewModelFactory

/**
 * Punto de entrada de la aplicación Planify.
 * Instancia los ViewModels y configura el tema + grafo de navegación Compose.
 */
class MainActivity : ComponentActivity() {

    // LoginViewModel no necesita factory (sin parámetros)
    private val loginViewModel: LoginViewModel by viewModels()

    // TaskViewModel necesita TaskRepository → usamos factory
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanifyTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController  = navController,
                    loginViewModel = loginViewModel,
                    taskViewModel  = taskViewModel
                )
            }
        }
    }
}
