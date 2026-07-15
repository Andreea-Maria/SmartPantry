package com.example.smartpantry

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smartpantry.presentation.addproduct.AddProductScreen
import com.example.smartpantry.presentation.addproduct.ProductLookupViewModel
import com.example.smartpantry.presentation.auth.AuthScreen
import com.example.smartpantry.presentation.auth.AuthViewModel
import com.example.smartpantry.presentation.barcode.BarcodeScanScreen
import com.example.smartpantry.presentation.editproduct.EditProductScreen
import com.example.smartpantry.presentation.home.HomeScreen
import com.example.smartpantry.presentation.home.ProductViewModel
import com.example.smartpantry.presentation.navigation.Screen
import com.example.smartpantry.presentation.scan.ScanScreen
import com.example.smartpantry.presentation.worker.ExpiryCheckWorker
import com.example.smartpantry.ui.theme.SmartPantryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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

                val productViewModel: ProductViewModel =
                    hiltViewModel()

                val productLookupViewModel: ProductLookupViewModel =
                    hiltViewModel()

                val authViewModel: AuthViewModel =
                    hiltViewModel()

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
                               viewModel = productViewModel,
                               onAddClick = {
                                   navController.navigate(Screen.AddProduct.route)
                               },
                               onLogoutClick = {
                                   authViewModel.logout()
                               },
                               onProductClick = { productId ->
                                   navController.navigate(
                                       Screen.EditProduct.createRoute(productId)
                                   )
                               }
                           )
                       }

                       composable(Screen.AddProduct.route) { backStackEntry ->

                           val scannedDate by backStackEntry
                               .savedStateHandle
                               .getStateFlow<String?>(
                                   "scanned_date",
                                   null
                               )
                               .collectAsState()

                           val scannedBarcode by backStackEntry
                               .savedStateHandle
                               .getStateFlow<String?>(
                                   "barcode",
                                   null
                               )
                               .collectAsState()

                           AddProductScreen(
                               viewModel = productViewModel,
                               onProductAdded = {
                                   navController.popBackStack()
                               },
                               lookupViewModel = productLookupViewModel,
                               onBackClick = {
                                   navController.popBackStack()
                               },
                               onScanClick = {
                                   navController.navigate(Screen.Scan.route)
                               },
                               scannedDate = scannedDate,
                               scannedBarcode = scannedBarcode,
                               onBarcodeScanClick = {
                                   navController.navigate(Screen.BarcodeScan.route)
                               }
                           )
                       }

                       composable(Screen.Scan.route) {

                           ScanScreen(
                               onBackClick = {
                                   navController.popBackStack()
                               },
                               onDateDetected = { detectedDate ->

                                   navController.previousBackStackEntry
                                       ?.savedStateHandle
                                       ?.set(
                                           "scanned_date",
                                           detectedDate
                                       )

                                   navController.popBackStack()
                               }
                           )
                       }

                       composable(
                           route = Screen.EditProduct.route,
                           arguments = listOf(
                               navArgument("productId") {
                                   type = NavType.IntType
                               }
                           )
                       ) {
                           backStackEntry ->

                           val productId =
                               backStackEntry.arguments
                                   ?.getInt("productId")
                                   ?: return@composable

                           EditProductScreen(
                               productId = productId,
                               viewModel = productViewModel,
                               onProductUpdated = {
                                   navController.popBackStack()
                               },
                               onBackClick = {
                                   productViewModel.clearSelectedProduct()
                                   navController.popBackStack()
                               }
                           )
                       }

                       composable(route = Screen.BarcodeScan.route) {

                           BarcodeScanScreen(

                               onBackClick = {
                                   navController.popBackStack()
                               },
                               onBarcodeDetected = { barcode ->
                                   navController.previousBackStackEntry
                                       ?.savedStateHandle
                                       ?.set("barcode", barcode)

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