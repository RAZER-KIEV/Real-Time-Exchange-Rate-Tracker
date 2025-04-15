package com.example.real_timeexchangeratetracker.data.repository.curency

import android.util.Log
import com.example.real_timeexchangeratetracker.data.model.Currency
import com.example.real_timeexchangeratetracker.data.repository.curency.db.dao.CurrencyDao
import com.example.real_timeexchangeratetracker.data.repository.curency.db.dao.CurrencyRateRecordDao
import com.example.real_timeexchangeratetracker.data.repository.curency.network.CurrencyService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val currencyService: CurrencyService,
    private val currencyDao: CurrencyDao,
    private val currencyRateRecordDao: CurrencyRateRecordDao
) : CurrencyRepository {


    override fun getTopCurrencies(): Flow<List<Currency>> = flow {
        coroutineScope {
            val currenciesFromDb = currencyDao.getAllCurrencies()
            if (currenciesFromDb.isEmpty()) {
                val currAPI = currencyService.getCurrencies()
                currencyDao.insertAll(currAPI)
                emit(currAPI)
            } else {
                emit(currenciesFromDb)
            }
        }
    }

    override suspend fun refreshRates(dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) {
            Log.d("TAG", "refreshRates")
            val rates = currencyService.getCurrenciesRates()
            currencyRateRecordDao.insertAll(rates)
            rates.forEach { rate ->
                val currency = currencyDao.getCurrencyById(rate.currencyId)
                val copyCurr =
                    currency?.copy(
                        lastUpdated = rate.lastUpdated,
                        lastKnownValue = rate.lastKnownValue
                    )
                copyCurr?.let {
                    currencyDao.update(copyCurr)
                }
            }
        }
    }

    override fun getMonitoredCurrenciesFlow(): Flow<List<Currency>> {
        return currencyDao.getMonitoredCurrencies()
    }

    override fun searchCurrencies(query: String): Flow<List<Currency>> {
        return currencyDao.searchCurrenciesByName(query)
    }

    override suspend fun updateCurrency(currency: Currency) {
        currencyDao.update(currency)
    }
}