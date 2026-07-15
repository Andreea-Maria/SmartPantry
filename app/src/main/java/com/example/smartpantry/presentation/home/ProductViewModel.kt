package com.example.smartpantry.presentation.home

import androidx.camera.core.impl.utils.Optional
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpantry.data.local.ProductEntity
import com.example.smartpantry.data.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.StateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    private val sortOption = MutableStateFlow("Expiry")

    private val filterOption  = MutableStateFlow("All")

    private val _selectedProduct =
        MutableStateFlow<ProductEntity?>(null)

    val selectedProduct: StateFlow<ProductEntity?> =
        _selectedProduct

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateSortOption(option: String) {
        sortOption.value = option
    }

    fun updateFilterOption(option: String) {
        filterOption.value = option
    }

    val products = repository.getAllProducts()
        .combine(
            searchQuery
        ) { products, query ->

            if (query.isBlank()) {
                products
            } else {

                products.filter { product ->

                    product.name.contains(
                        query,
                        ignoreCase = true
                    ) ||
                            product.category.contains(
                                query,
                                ignoreCase = true
                            )
                }
            }
        }
        .combine(sortOption) { products, sort ->

            when (sort) {

                "Name" -> {
                    products.sortedBy {
                        it.name
                    }
                }


                "Expiry" -> {
                    products.sortedBy {
                        it.expiryDate
                    }
                }

                else -> products
            }
        }
        .combine(filterOption) { products, filter ->

            val currentTime = System.currentTimeMillis()
            val sevenDays = 7L * 24 * 60 * 60 * 1000

            when (filter) {

                "Expired" -> {
                    products.filter {
                        it.expiryDate < currentTime
                    }
                }

                "Soon" -> {
                    products.filter {
                        it.expiryDate in
                                currentTime..(currentTime + sevenDays)
                    }
                }

                else -> products
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalProducts = products
        .map { it.size }

    val expiredProducts = products
        .map { productList ->

            val currentTime =
                System.currentTimeMillis()

            productList.count {
                it.expiryDate < currentTime
            }
        }

    val expiringSoonProducts = products
        .map { productList ->

            val currentTime =
                System.currentTimeMillis()

            val sevenDays =
                7L * 24* 60 * 60 * 1000

            productList.count {

                it.expiryDate in
                        currentTime..(currentTime + sevenDays)
            }
        }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun loadProduct(productId: Int) {
        viewModelScope.launch {
            _selectedProduct.value =
                repository.getProductById(productId)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }

}