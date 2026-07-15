package com.example.smartpantry.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "",
    val name: String,
    val category: String,
    val expiryDate: Long,
    val quantity: Int,
    val barcode: String? = null,
    val notes: String? = null
)