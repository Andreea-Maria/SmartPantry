package com.example.smartpantry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.internal.composableLambda
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.smartpantry.data.repository.DatabaseProvider
import com.example.smartpantry.data.repository.ProductRepository
import com.example.smartpantry.presentation.addproduct.AddProductScreen
import com.example.smartpantry.presentation.home.HomeScreen
import com.example.smartpantry.presentation.home.ProductViewModel
import com.example.smartpantry.presentation.home.ProductViewModelFactory
import com.example.smartpantry.presentation.navigation.Screen
import com.example.smartpantry.ui.theme.SmartPantryTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartpantry.presentation.addproduct.AddProductScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val database = DatabaseProvider.getDatabase(this)
        val repository = ProductRepository(database.productDao())

        setContent {

            SmartPantryTheme {

                val viewModel: ProductViewModel = viewModel(
                    factory = ProductViewModelFactory(repository)
                )

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {

                    composable(Screen.Home.route) {

                        HomeScreen(
                            viewModel = viewModel,
                            onAddClick = {
                                navController.navigate(Screen.AddProduct.route)
                            }
                        )
                    }

                    composable(Screen.AddProduct.route) {
                        AddProductScreen(
                            viewModel = viewModel,
                            onProductAdded = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}