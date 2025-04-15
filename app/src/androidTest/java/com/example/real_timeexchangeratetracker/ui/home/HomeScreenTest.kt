package com.example.real_timeexchangeratetracker.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.real_timeexchangeratetracker.data.model.Currency
import com.example.real_timeexchangeratetracker.data.repository.curency.FakeCurrencyRepository
import com.example.real_timeexchangeratetracker.domain.CurrencyViewModel
import com.example.real_timeexchangeratetracker.ui.theme.RealTimeExchangeRateTrackerTheme
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: CurrencyViewModel
    private lateinit var fakeRepository: FakeCurrencyRepository

    // Sample data
    private val currencyUSD = Currency(1L, "USD", "US Dollar", 1.0, 0L, false, "", true)
    private val currencyBTC = Currency(3L, "BTC", "Bitcoin", 60000.0, 0L, true, "", true)


    @Before
    fun setUp() {
        fakeRepository = FakeCurrencyRepository()
        viewModel =
            CurrencyViewModel(fakeRepository)
        fakeRepository.setMonitoredCurrencies(listOf(currencyUSD, currencyBTC))
    }

    fun setContent() {
        composeTestRule.setContent {
            RealTimeExchangeRateTrackerTheme {
                HomeScreen(
                    onAddClicked = {},
                    viewModel = viewModel
                )
            }
        }
    }

    @Test
    fun topAppBar_isDisplayed_withTitleAndAddButton() {
        setContent()
        composeTestRule.onNodeWithText("Exchange Rates")
            .assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add").assertIsDisplayed()
    }

    @Test
    fun monitoredCurrencies_areDisplayed_inList() {
        setContent()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(text = currencyUSD.shortName, substring = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(text = currencyUSD.longName, substring = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(text = currencyBTC.shortName, substring = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(text = currencyBTC.longName, substring = true)
            .assertIsDisplayed()

        val expectedUsdValueText = String.format("%.4f", currencyUSD.lastKnownValue)
        composeTestRule.onNodeWithText(text = expectedUsdValueText, substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun clickAddButton_triggersAction() {
        var addClicked = false
        composeTestRule.setContent {
            RealTimeExchangeRateTrackerTheme {
                HomeScreen(
                    onAddClicked = { addClicked = true },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.waitForIdle()
        assert(addClicked) { "Add button click action was not triggered" }
    }

    @Test
    fun swipeToDismiss_removesItem_callsViewModel() {
        setContent()
        composeTestRule.waitForIdle()
        val usdNode = composeTestRule.onNodeWithText(text = currencyUSD.shortName, substring = true)
        usdNode.performTouchInput { swipeRight() }
        composeTestRule.waitForIdle()
        val expectedUpdate = currencyUSD.copy(isMonitored = false)
        assertEquals(expectedUpdate, fakeRepository.updateCalledWith)
    }
}