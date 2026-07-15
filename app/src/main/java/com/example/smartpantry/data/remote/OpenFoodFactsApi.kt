package com.example.smartpantry.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {

    @GET("api/v2/product/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode")
        barcode: String,

        @Query("fields")
        fields: String = "product_name,categories,brands",

        @Header("User-Agent")
        userAgent: String = "SmartPantry-Android/1.0"
    ): OpenFoodFactsResponse
}