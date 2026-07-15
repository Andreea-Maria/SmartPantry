package com.example.smartpantry.di

import android.content.Context
import androidx.room.Room
import com.example.smartpantry.data.local.ProductDao
import com.example.smartpantry.data.local.SmartPantryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SmartPantryDatabase {

        return Room.databaseBuilder(
            context,
            SmartPantryDatabase::class.java,
            "smart_pantry_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideProductDao(
        database: SmartPantryDatabase
    ): ProductDao {

        return database.productDao()
    }
}