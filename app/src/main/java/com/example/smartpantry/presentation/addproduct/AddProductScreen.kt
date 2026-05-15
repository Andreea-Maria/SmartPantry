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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment

@Composable
fun AddProductScreen(
    viewModel: ProductViewModel,
    onProductAdded: () -> Unit,
    onBackClick: () -> Unit,
    onScanClick: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false)}
    var quantityError by remember { mutableStateOf(false) }
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
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable {
                        onBackClick()
                    }
                    .padding(end = 12.dp)
            )

            Text(
                text = "AddProduct",
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

        if(nameError) {
            Text(
                text = "Please enter product name",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

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

        if(quantityError) {
            Text(
                text = "Please enter a valid quantity",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

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

            Text(
                text = "Select Epiry Date"
            )
        }

        OutlinedButton(
            onClick = {
                onScanClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scan Expiry Date")
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Selected expiry date",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatDate(expiryDate),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Button(
            onClick = {
                nameError = name.isBlank()

                quantityError =
                    quantity.toIntOrNull() == null ||
                            quantity.toInt() <= 0

                if (nameError || quantityError) {
                    return@Button
                }

                val product = ProductEntity(
                    name = name,
                    category = category,
                    quantity = quantity.toInt(),
                    expiryDate = expiryDate
                )

                viewModel.addProduct(product)

                onProductAdded()
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

            Text("Save Product")
        }
    }
}