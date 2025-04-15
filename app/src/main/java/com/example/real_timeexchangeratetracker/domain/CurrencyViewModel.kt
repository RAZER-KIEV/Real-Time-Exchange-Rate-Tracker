package com.example.real_timeexchangeratetracker.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.real_timeexchangeratetracker.data.model.Currency
import com.example.real_timeexchangeratetracker.data.repository.curency.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    val monitoredCurrencies: StateFlow<List<Currency>> =
        currencyRepository.getMonitoredCurrenciesFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCurrencies: StateFlow<List<Currency>> =
        currencyRepository.getTopCurrencies()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow: SharedFlow<String> = _errorFlow.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")

    private var _searchedCurrencies: MutableStateFlow<List<Currency>> =
        MutableStateFlow(emptyList())
    val searchedCurrencies: StateFlow<List<Currency>> = _searchedCurrencies

    private val _lastUpdated = MutableSharedFlow<String>()

    private var isMonitoringStarted = false

    private var rateFetchingJob: Job? = null

    init {
        Log.d(" TAG", "initCurrencyViewModel $this")
        addAdditionalMonitoredCurrenciesCollector()
    }

    fun updateCurrency(currency: Currency) {
        viewModelScope.launch {
            currencyRepository.updateCurrency(currency)
        }
    }

    // Call this function from where monitoring should actually begin (e.g., UI lifecycle)
    fun startMonitoringRatesIfNeeded() {
        viewModelScope.launch {
            // Check if already started and if there's anything to monitor
            if (!isMonitoringStarted && monitoredCurrencies.value.isNotEmpty()) {
                isMonitoringStarted = true
                startFetchingCurrencyRatesPeriodically()
            }
        }
    }

    private fun startFetchingCurrencyRatesPeriodically() {
        Log.d("TAG", "startFetchingCurrencyRatesPeriodically")
        if (!isMonitoringStarted || rateFetchingJob?.isActive == true) return // Prevent relaunch

        rateFetchingJob = viewModelScope.launch { // <-- Assign Job here
            while (isActive) {
                try {
                    currencyRepository.refreshRates()
                } catch (e: Exception) {
                    ensureActive()
                    onError("Error fetching currency rates: ${e.message}")
                }
                delay(TimeUnit.SECONDS.toMillis(5))
            }
        }
        rateFetchingJob?.invokeOnCompletion { throwable ->
            Log.d("TAG", "Rate fetching job completed: ${throwable?.message ?: "OK"}")
        }
    }

    private fun addAdditionalMonitoredCurrenciesCollector() {
        Log.d("TAG", "addAdditionalMonitoredCurrenciesCollector")
        viewModelScope.launch {
            monitoredCurrencies
                .debounce(100L)
                .collectLatest { currencies ->
                    Log.d(
                        "TAG",
                        "Monitored currencies updated in collector: ${currencies.size} items"
                    )
                    if (currencies.isEmpty() && isMonitoringStarted) {
                        isMonitoringStarted = false
                        rateFetchingJob?.cancel()
                        rateFetchingJob = null
                        _lastUpdated.emit(System.currentTimeMillis().toString())
                        Log.d("TAG", "Monitoring potentially stopped (list empty)")
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
            val res = _searchQuery
                .flatMapLatest { query ->
                    viewModelScope.async(Dispatchers.IO) {
                        currencyRepository.searchCurrencies(query)
                    }.await()
                }
            val list = res.first()
            _searchedCurrencies.value = list
        }
    }

    fun onClearSearch() {
        viewModelScope.launch {
            _searchQuery.value = ""
            _searchedCurrencies.value = emptyList()
        }
    }

    private fun onError(text: String) {
        viewModelScope.launch {
            Log.e("TAG", text)
            _errorFlow.emit(text)
        }
    }

    @VisibleForTesting
    internal fun getRateFetchingJob(): Job? {
        return rateFetchingJob
    }

    override fun onCleared() {
        super.onCleared()
        rateFetchingJob?.cancel()
        Log.d("TAG", "ViewModel cleared, cancelling rate fetching job.")
    }
}