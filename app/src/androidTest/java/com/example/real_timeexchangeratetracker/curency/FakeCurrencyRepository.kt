package com.example.real_timeexchangeratetracker.data.repository.curency

import com.example.real_timeexchangeratetracker.data.model.Currency
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// Fake Repository Implementation for testing
open class FakeCurrencyRepository : CurrencyRepository {
    private val monitoredCurrenciesFlow = MutableStateFlow<List<Currency>>(emptyList())
    private val allCurrenciesFlow = MutableStateFlow<List<Currency>>(emptyList())
    private val searchResultsFlow = MutableStateFlow<List<Currency>>(emptyList())
    var updateCalledWith: Currency? = null
    var refreshRatesCallCount = 0 // Changed to count calls
    var lastSearchQuery: String? = null

    override fun getTopCurrencies(): Flow<List<Currency>> = allCurrenciesFlow
    override suspend fun updateCurrency(currency: Currency) {
        updateCalledWith = currency
        val currentMonitored = monitoredCurrenciesFlow.value.toMutableList()
        val indexMonitored = currentMonitored.indexOfFirst { it.id == currency.id }
        if (currency.isMonitored) {
            if (indexMonitored == -1) currentMonitored.add(currency) else currentMonitored[indexMonitored] =
                currency
        } else {
            if (indexMonitored != -1) currentMonitored.removeAt(indexMonitored)
        }
        monitoredCurrenciesFlow.value = currentMonitored

        val currentAll = allCurrenciesFlow.value.toMutableList()
        val indexAll = currentAll.indexOfFirst { it.id == currency.id }
        if (indexAll != -1) {
            currentAll[indexAll] = currency
            allCurrenciesFlow.value = currentAll
        }
    }

    // Simulate refresh call
    override suspend fun refreshRates(dispatcher: CoroutineDispatcher) {
        refreshRatesCallCount++
    }

    override fun getMonitoredCurrenciesFlow(): Flow<List<Currency>> = monitoredCurrenciesFlow
    override fun searchCurrencies(query: String): Flow<List<Currency>> {
        lastSearchQuery = query
        searchResultsFlow.value = allCurrenciesFlow.value.filter {
            it.shortName.contains(query, ignoreCase = true) || it.longName.contains(
                query,
                ignoreCase = true
            )
        }
        return searchResultsFlow
    }

    fun setMonitoredCurrencies(currencies: List<Currency>) {
        monitoredCurrenciesFlow.value = currencies
    }

    fun emitErrorInRefresh() { // Helper for testing error flow
        throw RuntimeException("Fake network error")
    }
}