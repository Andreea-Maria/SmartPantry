package com.example.smartpantry.data.repository

import com.example.smartpantry.data.local.ProductDao
import com.example.smartpantry.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao
) {

    fun getAllProducts(): Flow<List<ProductEntity>> {
        return productDao.getAllProducts()
    }

    suspend fun insertProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity){
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity){
        productDao.deleteProduct(product)
    }

    suspend fun getProductById(id: Int): ProductEntity? {
        return productDao.getProductById(id)
    }
}