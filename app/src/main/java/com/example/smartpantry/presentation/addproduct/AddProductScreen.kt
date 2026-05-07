package com.example.smartpantry.presentation.addproduct

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartpantry.data.local.ProductEntity
import com.example.smartpantry.presentation.home.ProductViewModel

@Composable
fun AddProductScreen(
    viewModel: ProductViewModel,
    onProductAdded: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

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

                val product = ProductEntity(
                    name = name,
                    category = category,
                    quantity = quantity.toIntOrNull() ?: 1,
                    expiryDate = System.currentTimeMillis()
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