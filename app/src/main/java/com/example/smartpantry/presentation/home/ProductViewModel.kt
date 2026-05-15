package com.example.smartpantry.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpantry.data.local.ProductEntity
import com.example.smartpantry.data.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    val products = repository.getAllProducts()
        .combine(searchQuery) { products, query ->
            if (query.isBlank()) {
                products
            } else {
                products.filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                            product.category.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
}

class ProductViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelclass: Class<T>): T{
        return ProductViewModel(repository) as T
    }
}