package com.example.real_timeexchangeratetracker.data.repository.curency.network

import com.example.real_timeexchangeratetracker.data.model.Currency
import com.example.real_timeexchangeratetracker.data.model.CurrencyRateRecord

// Todo. Apply Retrofit.
interface CurrencyService {
    suspend fun getCurrencies(): List<Currency>
    suspend fun getCurrenciesRates(): List<CurrencyRateRecord>
}