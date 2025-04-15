package com.example.real_timeexchangeratetracker.di.moduls

import android.content.Context
import androidx.room.Room
import com.example.real_timeexchangeratetracker.data.repository.curency.CurrencyRepository
import com.example.real_timeexchangeratetracker.data.repository.curency.CurrencyRepositoryImpl
import com.example.real_timeexchangeratetracker.data.repository.curency.db.AppDatabase
import com.example.real_timeexchangeratetracker.data.repository.curency.db.dao.CurrencyDao
import com.example.real_timeexchangeratetracker.data.repository.curency.db.dao.CurrencyRateRecordDao
import com.example.real_timeexchangeratetracker.data.repository.curency.network.CurrencyService
import com.example.real_timeexchangeratetracker.data.repository.curency.network.FakeCurrencyService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext, AppDatabase::class.java, "currency_database"
        ).build()
    }

    @Provides
    fun provideCurrencyDao(appDatabase: AppDatabase): CurrencyDao {
        return appDatabase.currencyDao()
    }

    @Provides
    fun provideCurrencyStatusDao(appDatabase: AppDatabase): CurrencyRateRecordDao {
        return appDatabase.currencyRateRecordDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://api.example.com/") // Your base URL
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): CurrencyService {
        return FakeCurrencyService()
//        return retrofit.create(CurrencyService::class.java) //todo: use retrofit if suitable API be provided.
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule { //actually used by Hilt.

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        someRepositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository
}