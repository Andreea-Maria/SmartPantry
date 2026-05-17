package com.example.smartpantry.presentation.home

import android.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartpantry.data.local.ProductEntity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.foundation.background
import androidx.compose.runtime.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ProductViewModel,
    onAddClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    val products by viewModel.products.collectAsState()

    val totalProducts by
            viewModel.totalProducts.collectAsState(initial = 0)

    val expiredProducts by
            viewModel.expiredProducts.collectAsState(initial = 0)

    val expiringSoonProducts by
            viewModel.expiringSoonProducts.collectAsState(initial = 0)

    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Smart Pantry")
                },
                actions = {

                    TextButton(
                        onClick = {
                            onLogoutClick()
                        }
                    ) {
                        Text("Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick
            ) {
                Text("+")
            }
        }
    ) { padding ->

        if (products.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No products added yet")
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        StatisticCard(
                            title = "Total",
                            value = totalProducts.toString(),
                            modifier = Modifier.weight(1f)
                        )

                        StatisticCard(
                            title = "Expired",
                            value = expiredProducts.toString(),
                            modifier = Modifier.weight(1f)
                        )

                        StatisticCard(
                            title = "Soon",
                            value = expiringSoonProducts.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { newValue ->
                            searchText = newValue
                            viewModel.updateSearchQuery(newValue)
                        },
                        label = {
                            Text("Search products")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(
                    items = products,
                    key = { product -> product.id }
                ) { product ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart)  {
                                viewModel.deleteProduct(product)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFFFCDD2))
                                    .padding(16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text("Delete")
                            }
                        },
                        content = {
                            ProductItem(
                                product = product,
                                onDelete = {
                                    viewModel.deleteProduct(product)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = value,
                style =
                    MaterialTheme.typography.headlineMedium
            )

            Text(
                text = title
            )
        }
    }
}

@Composable
fun ProductItem(
    product: ProductEntity,
    onDelete: () -> Unit
) {

    val currentTime = System.currentTimeMillis()

    val cardColor = when {

        product.expiryDate < currentTime -> {
            Color(0xFFFFCDD2)
        }

        product.expiryDate - currentTime < 3 * 24 * 60 * 60 * 1000 -> {
            Color(0xFFFFF9C4)
        }

        else -> {
            Color(0xFFC8E6C9)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Category: ${product.category}"
            )

            Text(
                text = "Quantity: ${product.quantity}"
            )

            Text(
                text = "Expires: ${formatDate(product.expiryDate)}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDelete
            ) {
                Text("Delete")
            }
        }
    }
}