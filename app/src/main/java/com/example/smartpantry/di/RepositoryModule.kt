package com.example.smartpantry.di

import com.example.smartpantry.data.auth.AuthRepository
import com.example.smartpantry.data.local.ProductDao
import com.example.smartpantry.data.repository.ProductLookupRepository
import com.example.smartpantry.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: ProductDao
    ): ProductRepository {
        return ProductRepository(productDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun provideProductLookupRepository(): ProductLookupRepository {
        return ProductLookupRepository()
    }
}