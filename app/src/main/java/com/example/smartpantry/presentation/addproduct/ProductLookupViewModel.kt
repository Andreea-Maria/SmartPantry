package com.example.smartpantry.presentation.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpantry.data.remote.OpenFoodProduct
import com.example.smartpantry.data.repository.ProductLookupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductLookupUiState(
    val isLoading: Boolean = false,
    val product: OpenFoodProduct? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ProductLookupViewModel @Inject constructor(
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