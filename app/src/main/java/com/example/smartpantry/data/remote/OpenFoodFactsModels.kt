package com.example.smartpantry.data.remote

import com.google.gson.annotations.SerializedName

data class OpenFoodFactsResponse(
    val status: Int,
    val product: OpenFoodProduct?
)

data class OpenFoodProduct(
    @SerializedName("product_name")
    val productName: String?,

    @SerializedName("categories")
    val categories: String?,

    @SerializedName("brands")
    val brands: String?
)