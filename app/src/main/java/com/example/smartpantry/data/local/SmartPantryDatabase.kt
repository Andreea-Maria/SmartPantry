package com.example.smartpantry.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class],
    version = 3,
    exportSchema = false
)
abstract class SmartPantryDatabase : RoomDatabase(){

    abstract fun productDao(): ProductDao
}