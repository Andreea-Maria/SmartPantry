package com.example.smartpantry.presentation.addproduct

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartpantry.data.local.ProductEntity
import com.example.smartpantry.presentation.home.ProductViewModel
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.ui.platform.LocalContext
import com.example.smartpantry.presentation.home.formatDate
import java.util.Calendar

@Composable
fun AddProductScreen(
    viewModel: ProductViewModel,
    onProductAdded: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var expiryDate by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    val context = LocalContext.current

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year:  Int, month: Int, dayOfMonth: Int ->

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
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Add Product",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Product name")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
            },
            label = {
                Text("Category")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = {
                quantity = it
            },
            label = {
                Text("Quantity")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                datePickerDialog.show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Select Epiry Date"
            )
        }

        Text(
            text = "Selected: ${formatDate(expiryDate)}"
        )

        Button(
            onClick = {

                val product = ProductEntity(
                    name = name,
                    category = category,
                    quantity = quantity.toIntOrNull() ?: 1,
                    expiryDate = expiryDate
                )

                viewModel.addProduct(product)

                onProductAdded()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Product")
        }
    }
}