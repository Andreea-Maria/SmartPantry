package com.example.smartpantry.presentation.barcode

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.experimental.Experimental
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlin.contracts.contract

@Composable
fun BarcodeScanScreen(
    onBackClick: () -> Unit,
    onBarcodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var detectedBarcode by remember {
        mutableStateOf("")
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Scan Barcode",
            style = MaterialTheme.typography.headlineLarge
        )

        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)

                    val cameraProviderFuture =
                        ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener(
                        {
                            val cameraProvider =
                                cameraProviderFuture.get()

                            val preview =
                                Preview.Builder()
                                    .build()

                            preview.surfaceProvider =
                                previewView.surfaceProvider

                            val imageAnalysis =
                                ImageAnalysis.Builder()
                                    .build()

                            val scanner =
                                BarcodeScanning.getClient()

                            imageAnalysis.setAnalyzer(
                                ContextCompat.getMainExecutor(ctx)
                            ) { imageProxy ->
                                processBarcodeImage(
                                    scanner = scanner,
                                    imageProxy = imageProxy,
                                    onBarcodeDetected = { barcode ->
                                        if (barcode.isNotBlank()) {
                                            detectedBarcode = barcode
                                        }
                                    }
                                )
                            }

                            try {
                                cameraProvider.unbindAll()

                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        },
                        ContextCompat.getMainExecutor(ctx)
                    )

                    previewView
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
        } else {
            Text("Camera permission denied")
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Detected Barcode",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = detectedBarcode.ifBlank {
                        "No barcode detected yet"
                    }
                )
            }
        }

        if (detectedBarcode.isNotBlank()) {
            Button(
                onClick = {
                    onBarcodeDetected(detectedBarcode)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Barcode")
            }
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@androidx.annotation.OptIn(
    markerClass = [ExperimentalGetImage::class]
)
private fun processBarcodeImage(
    scanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onBarcodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image

    if (mediaImage == null) {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(
        mediaImage,
        imageProxy.imageInfo.rotationDegrees
    )

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            val rawValue =
                barcodes.firstOrNull()
                    ?.rawValue
                    .orEmpty()

            if (rawValue.isNotBlank()) {
                onBarcodeDetected(rawValue)
            }
        }
        .addOnFailureListener { exception ->
            exception.printStackTrace()
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}