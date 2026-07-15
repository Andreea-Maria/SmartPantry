package com.example.smartpantry.presentation.editproduct

import android.R
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartpantry.data.local.ProductEntity
import com.example.smartpantry.presentation.home.ProductViewModel
import com.example.smartpantry.presentation.home.formatDate
import com.example.smartpantry.presentation.navigation.Screen
import java.util.Calendar


@Composable
fun EditProductScreen(
    productId: Int,
    viewModel: ProductViewModel,
    onProductUpdated: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val selectedProduct by viewModel.selectedProduct.collectAsState()

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var expiryDate by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var nameError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }
    var barcode by remember { mutableStateOf("") }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(selectedProduct) {
        selectedProduct?.let { product ->
            name = product.name
            category = product.category
            quantity = product.quantity.toString()
            expiryDate = product.expiryDate
            barcode = product.barcode.orEmpty()
        }
    }

    val calendar = Calendar.getInstance().apply {
        timeInMillis = expiryDate
    }

    val datePickerDialog = DatePickerDialog(
        context,
        {_: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val selectedCalendar = Calendar.getInstance()

            selectedCalendar.set(
                year,
                month,
                dayOfMonth
            )

            expiryDate = selectedCalendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Edit Product",
                style = MaterialTheme.typography.headlineLarge
            )
        }

            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    name = newValue
                    nameError = false
                },
                label = {
                    Text("Product name")
                },
                isError = nameError,
                modifier = Modifier.fillMaxWidth()
            )

            if (nameError) {
                Text(
                    text = "Please enter a product name",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = category,
                onValueChange = { newValue ->
                    category = newValue
                },
                label = {
                    Text("Category")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = quantity,
                onValueChange = { newValue ->
                    quantity = newValue
                    quantityError = false
                },
                label = {
                    Text("Quantity")
                },
                isError = quantityError,
                modifier = Modifier.fillMaxWidth()
            )

            if (quantityError) {
                Text(
                    text = "Please enter a valid quantity",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = barcode,
                onValueChange = { newValue ->
                    barcode = newValue
                },
                label = {
                    Text("Barcode")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    datePickerDialog.show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Select Ecpiry Date")
            }

            Text(
                text = "Selected expiry date: ${formatDate(expiryDate)}"
            )

            Button(
                onClick = {
                    nameError = name.isBlank()

                    quantityError = quantity.toIntOrNull() == null ||
                            quantity.toInt() <= 0

                    if (nameError || quantityError) {
                        return@Button
                    }

                    val updatedProduct = ProductEntity(
                        id = productId,
                        userId = selectedProduct?.userId.orEmpty(),
                        name = name,
                        category = category,
                        quantity = quantity.toInt(),
                        expiryDate = expiryDate,
                        barcode = barcode.trim().ifBlank { null },
                        notes = selectedProduct?.notes
                    )

                    viewModel.updateProduct(updatedProduct)
                    viewModel.clearSelectedProduct()

                    onProductUpdated()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Save Changes")
            }
    }
}