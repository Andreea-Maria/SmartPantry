package com.example.smartpantry.data.repository

import com.example.smartpantry.data.remote.OpenFoodFactsClient
import com.example.smartpantry.data.remote.OpenFoodProduct

class ProductLookupRepository {

    suspend fun getProductByBarcode(
        barcode: String
    ): Result<OpenFoodProduct> {
        return try {
            val response =
                OpenFoodFactsClient.api
                    .getProductByBarcode(barcode)

            val product = response.product

            if (
                response.status ==1 &&
                product != null
            ) {
                Result.success(product)
            } else {
                Result.failure(
                    Exception("Product not found")
                )
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}