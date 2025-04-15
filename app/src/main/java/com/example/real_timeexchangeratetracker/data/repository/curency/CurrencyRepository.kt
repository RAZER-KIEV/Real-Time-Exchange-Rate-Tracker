package com.example.real_timeexchangeratetracker.data.repository.curency

import com.example.real_timeexchangeratetracker.data.model.Currency
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getTopCurrencies(): Flow<List<Currency>>
    suspend fun updateCurrency(currency: Currency)
    suspend fun refreshRates(dispatcher: CoroutineDispatcher = Dispatchers.IO)
    fun getMonitoredCurrenciesFlow(): Flow<List<Currency>>
    fun searchCurrencies(query: String): Flow<List<Currency>>
}