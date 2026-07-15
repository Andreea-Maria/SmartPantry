package com.example.smartpantry.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SmartPantryDatabase : RoomDatabase(){

    abstract fun productDao(): ProductDao
}