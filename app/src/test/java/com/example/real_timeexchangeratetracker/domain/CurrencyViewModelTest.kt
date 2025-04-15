package com.example.real_timeexchangeratetracker.domain

import app.cash.turbine.test
import com.example.real_timeexchangeratetracker.data.model.Currency
import com.example.real_timeexchangeratetracker.data.repository.curency.FakeCurrencyRepository
import com.example.real_timeexchangeratetracker.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class CurrencyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CurrencyViewModel
    private lateinit var fakeRepository: FakeCurrencyRepository

    // Sample data
    private val currencyUSD = Currency(1L, "USD", "US Dollar", 1.0, 0L, false, "", true)
    private val currencyEUR = Currency(2L, "EUR", "Euro", 0.9, 0L, false, "", false)
    private val currencyBTC = Currency(3L, "BTC", "Bitcoin", 60000.0, 0L, true, "", true)
    private val currencyETH = Currency(4L, "ETH", "Ethereum", 3000.0, 0L, true, "", false)

    @Before
    fun setUp() {
        fakeRepository = FakeCurrencyRepository()
        viewModel = CurrencyViewModel(fakeRepository)
    }

    @Test
    fun `initial monitored currencies are collected`() = runTest {
        val initialMonitored = listOf(currencyUSD, currencyBTC)
        fakeRepository.setMonitoredCurrencies(initialMonitored)

        viewModel.monitoredCurrencies.test {
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(initialMonitored, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial all currencies are collected`() = runTest {
        val initialAll = listOf(currencyUSD, currencyEUR, currencyBTC, currencyETH)
        fakeRepository.setAllCurrencies(initialAll)

        viewModel.allCurrencies.test {
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(initialAll, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateCurrency calls repository update`() = runTest {
        val currencyToUpdate = currencyEUR.copy(isMonitored = true)
        viewModel.updateCurrency(currencyToUpdate)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(currencyToUpdate, fakeRepository.updateCalledWith)
    }

    @Test
    fun `startMonitoringRatesIfNeeded does NOT start loop when monitored list is empty`() =
        runTest {
            fakeRepository.setMonitoredCurrencies(emptyList())
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.startMonitoringRatesIfNeeded()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                0,
                fakeRepository.refreshRatesCallCount
            )
        }


    @After
    fun cleanup() {
        viewModel.getRateFetchingJob()?.cancel()
        // Note: Need access to viewModel instance here, might require adjustment
        // if viewModel is lateinit and not initialized in some test setups.
    }
}
