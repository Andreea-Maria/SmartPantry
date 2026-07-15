package com.example.smartpantry.presentation.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartpantry.data.remote.OpenFoodProduct
import com.example.smartpantry.data.repository.ProductLookupRepository
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductLookupUiState(
    val isLoading: Boolean = false,
    val product: OpenFoodProduct? = null,
    val errorMessage: String? = null
)

class ProductLookupViewModel(
    private val repository: ProductLookupRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ProductLookupUiState())

    val uiState: StateFlow<ProductLookupUiState> =
        _uiState

    fun lookupBarcode(barcode: String) {
        if (barcode.isBlank()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = ProductLookupUiState(
                isLoading = true
            )

            val result =
                repository.getProductByBarcode(
                    barcode.trim()
                )

            _uiState.value =
                result.fold(
                    onSuccess = { product ->
                        ProductLookupUiState(
                            product = product
                        )
                    },
                    onFailure = { exception ->
                        ProductLookupUiState(
                            errorMessage =
                                exception.message
                                    ?: "Could not load product"
                        )
                    }
                )
        }
    }

    fun clearResult() {
        _uiState.value = ProductLookupUiState()
    }
}

class ProductLookupViewModelFactory(
    private val repository: ProductLookupRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                ProductLookupViewModel::class.java
            )
        ) {
            return ProductLookupViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}