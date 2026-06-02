package com.example.planify.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Profile : Screen("profile")
    object Tasks : Screen("tasks")
}
