package com.example.smartpantry.presentation.navigation

sealed class Screen(val route: String) {

    data object Home : Screen("home")

    data object AddProduct : Screen("add_product")

    data object Scan : Screen("Scan")
}