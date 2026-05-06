package com.example.smartpantry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartpantry.data.repository.DatabaseProvider
import com.example.smartpantry.data.repository.ProductRepository
import com.example.smartpantry.presentation.home.HomeScreen
import com.example.smartpantry.presentation.home.ProductViewModel
import com.example.smartpantry.presentation.home.ProductViewModelFactory
import com.example.smartpantry.ui.theme.SmartPantryTheme

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

                HomeScreen(viewModel = viewModel)
            }
        }
    }
}