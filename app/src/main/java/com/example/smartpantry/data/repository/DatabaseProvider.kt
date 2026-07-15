package com.example.smartpantry.data.repository

import android.content.Context
import androidx.room.Room
import com.example.smartpantry.data.local.SmartPantryDatabase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: SmartPantryDatabase? = null

    fun  getDatabase(context: Context): SmartPantryDatabase{
        return INSTANCE ?: synchronized(this){
            val instance = Room.databaseBuilder(
                context.applicationContext,
                SmartPantryDatabase::class.java,
                "smart_pantry_db"
            )
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            instance
        }
    }
}
