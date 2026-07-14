package com.example.smartpantry.presentation.navigation

sealed class Screen(val route: String) {

    data object Home : Screen("home")

    data object AddProduct : Screen("add_product")

    data object Scan : Screen("scan/{detectedDate") {

        fun createRoute(
            detectedDate: String
        ): String {
            return "scan/$detectedDate"
        }
    }

    data object EditProduct : Screen("edit_product/{productId}") {
        fun createRoute(productId: Int): String {
            return "edit_product/$productId"
        }
    }
}