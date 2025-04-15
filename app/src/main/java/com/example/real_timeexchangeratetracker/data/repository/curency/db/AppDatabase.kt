package com.example.real_timeexchangeratetracker.data.repository.curency.db

import com.example.real_timeexchangeratetracker.data.model.Currency


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.real_timeexchangeratetracker.data.model.CurrencyRateRecord
import com.example.real_timeexchangeratetracker.data.repository.curency.db.dao.CurrencyDao
import com.example.real_timeexchangeratetracker.data.repository.curency.db.dao.CurrencyRateRecordDao


@Database(
    entities = [Currency::class, CurrencyRateRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao

    abstract fun currencyRateRecordDao(): CurrencyRateRecordDao

    companion object {
        // Use @Volatile to ensure visibility across threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "currency_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
