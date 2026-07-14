package com.example.smartpantry.presentation.home

import android.R
import android.graphics.Paint
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ProductViewModel,
    onAddClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onProductClick: (Int) -> Unit
) {

    val products by viewModel.products.collectAsState()

    val totalProducts by
            viewModel.totalProducts.collectAsState(initial = 0)

    val expiredProducts by
            viewModel.expiredProducts.collectAsState(initial = 0)

    val expiringSoonProducts by
            viewModel.expiringSoonProducts.collectAsState(initial = 0)

    var searchText by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    var selectedSort by remember { mutableStateOf("Expiry") }

    var filterExpanded by remember { mutableStateOf(false) }

    var selectedFilter by remember { mutableStateOf("All") }

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

                item {

                    Box {

                        OutlinedButton(
                            onClick = {
                                expanded = true
                            }
                        ) {
                            Text("Sort: $selectedSort")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {

                            DropdownMenuItem(
                                text = {
                                    Text("Expiry")
                                },
                                onClick = {

                                    selectedSort = "Expiry"

                                    viewModel.updateSortOption(
                                        "Expiry"
                                    )

                                    expanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text("Name")
                                },
                                onClick = {

                                    selectedSort = "Name"

                                    viewModel.updateSortOption(
                                        "Name"
                                    )

                                    expanded = false
                                }
                            )
                        }
                    }
                }

                item {
                    Box {

                        OutlinedButton(
                            onClick = {
                                filterExpanded = true
                            }
                        ) {
                            Text("Filter: $selectedFilter")
                        }

                        DropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = {
                                filterExpanded = false
                            }
                        ) {

                            DropdownMenuItem(
                                text = {
                                    Text("All")
                                },
                                onClick = {
                                    selectedFilter = "All"
                                    viewModel.updateFilterOption("All")
                                    filterExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text("Expired")
                                },
                                onClick = {
                                    selectedFilter = "Expired"
                                    viewModel.updateFilterOption("Expired")
                                    filterExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text("Expiring Soon")
                                },
                                onClick = {
                                    selectedFilter = "Soon"
                                    viewModel.updateFilterOption("Soon")
                                    filterExpanded = false
                                }
                            )
                        }
                    }
                }

                if (products.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (
                                    searchText.isNotBlank() ||
                                    selectedFilter != "All"
                                ) {
                                    "No products match your filters"
                                } else {
                                    "No products added yet"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {

                    items(
                        items = products,
                        key = { product -> product.id }
                    ) { product ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
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
                                    },
                                    onClick = {
                                        onProductClick(product.id)
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
    onDelete: () -> Unit,
    onClick: () -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
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