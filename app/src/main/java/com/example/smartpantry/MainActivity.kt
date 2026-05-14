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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smartpantry.presentation.worker.ExpiryCheckWorker
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHost
import com.example.smartpantry.data.auth.AuthRepository
import com.example.smartpantry.presentation.auth.AuthScreen
import com.example.smartpantry.presentation.auth.AuthViewModel
import com.example.smartpantry.presentation.auth.AuthViewModelFactory
import androidx.compose.runtime.getValue


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val database = DatabaseProvider.getDatabase(this)
        val repository = ProductRepository(database.productDao())
        val authRepository = AuthRepository()
        val workRequest =
            OneTimeWorkRequestBuilder<ExpiryCheckWorker>()
                .build()

        WorkManager.getInstance(this)
            .enqueue(workRequest)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        } else {
            scheduleExpiryCheck()
        }

        setContent {

            SmartPantryTheme {

                val viewModel: ProductViewModel = viewModel(
                    factory = ProductViewModelFactory(repository)
                )

                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(authRepository)
                )

                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

               if(!isLoggedIn) {

                   AuthScreen(
                       authViewModel = authViewModel
                   )
               } else {

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
                               },
                               onLogoutClick = {
                                   authViewModel.logout()
                               }
                           )
                       }

                       composable(Screen.AddProduct.route) {

                           AddProductScreen(
                               viewModel = viewModel,
                               onProductAdded = {
                                   navController.popBackStack()
                               },
                               onBackClick = {
                                   navController.popBackStack()
                               }
                           )
                       }
                   }
               }
            }
        }
    }

    private fun scheduleExpiryCheck() {
        val workRequest =
            OneTimeWorkRequestBuilder<ExpiryCheckWorker>()
                .build()

        WorkManager.getInstance(this)
            .enqueue(workRequest)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (
            requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            scheduleExpiryCheck()
        }
    }

}