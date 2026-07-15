package com.example.smartpantry.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpantry.data.local.ProductEntity
import com.example.smartpantry.data.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    private val sortOption = MutableStateFlow("Expiry")

    private val filterOption  = MutableStateFlow("All")

    private val _selectedProduct =
        MutableStateFlow<ProductEntity?>(null)

    private val currentUserId = callbackFlow {

        val listener  =
            FirebaseAuth.AuthStateListener { auth ->
                trySend(
                    auth.currentUser?.uid
                )
            }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

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

    val products =
        currentUserId
            .flatMapLatest { userId ->

                if (userId.isNullOrBlank()) {
                    flowOf(emptyList())
                } else {
                    repository.getAllProducts(userId)
                }
            }
        .combine(searchQuery) { products, query ->

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
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            repository.insertProduct(
                product.copy(userId = userId)
            )
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
        val userId =
            firebaseAuth.currentUser?.uid
                ?: return

        viewModelScope.launch {
            repository.updateProduct(
                product.copy(
                    userId = userId
                )
            )
        }
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }

}