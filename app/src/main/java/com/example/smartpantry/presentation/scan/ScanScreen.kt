package com.example.smartpantry.presentation.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.Manifest
import android.content.pm.PackageManager
import android.media.Image
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.contracts.contract
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Image
import androidx.room.util.TableInfo
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import androidx.camera.core.ExperimentalGetImage

@Composable
fun ScanScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

    var detectedText by remember {
        mutableStateOf("")
    }

    var detectedDate by remember {
        mutableStateOf("")
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->

        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {

        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
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
            text = "Scan Expiry Date",
            style = MaterialTheme.typography.headlineLarge
        )

        if (hasCameraPermission) {

            AndroidView(
                factory = { ctx ->

                    val previewView = PreviewView(ctx)

                    val cameraProviderFuture =
                        ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({

                        val cameraProvider =
                            cameraProviderFuture.get()

                        val preview = Preview.Builder()
                            .build()

                        val imageAnalysis =
                            ImageAnalysis.Builder()
                                .build()

                        val recognizer =
                            TextRecognition.getClient(
                                TextRecognizerOptions.DEFAULT_OPTIONS
                            )

                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(ctx)
                        ) { imageProxy ->

                            processImageProxy(
                                recognizer,
                                imageProxy
                            ) { text ->

                                if(text.isNotBlank()) {
                                    detectedText = text
                                }

                                val extractedDate = extractExpiryDate(text)

                                if (extractedDate.isNotBlank()) {
                                    detectedDate = extractedDate
                                }
                            }
                        }

                        preview.surfaceProvider =
                            previewView.surfaceProvider

                        val cameraSelector =
                            CameraSelector.DEFAULT_BACK_CAMERA

                        try {

                            cameraProvider.unbindAll()

                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )

                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }

                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Detected Text",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = detectedText.ifBlank {
                            "No text detected yet"
                        }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Detected Expiry Date",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = detectedDate.ifBlank {
                            "No expiry date detected"
                        }
                    )
                }
            }
        } else {

            Text(
                text = "Camera permission denied"
            )
        }


        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    recognizer: com.google.mlkit.vision.text.TextRecognizer,
    imageProxy: ImageProxy,
    onTextDetected: (String) -> Unit
) {

    val mediaImage = imageProxy.image

    if (mediaImage != null) {

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        recognizer.process(image)
            .addOnSuccessListener { visionText ->

                val detectedText = visionText.text

                onTextDetected(detectedText)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }

    } else {
        imageProxy.close()
    }
}

private fun extractExpiryDate(
    text: String
): String {

    val regex =
        Regex(
            """\b\d{2}[./-]\d{2}[./-]\d{4}\b"""
        )

    return regex.find(text)?.value ?: ""
}