package com.example.real_timeexchangeratetracker.data.repository.curency.network

import com.example.real_timeexchangeratetracker.data.model.Currency
import com.example.real_timeexchangeratetracker.data.model.CurrencyRateRecord
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FakeCurrencyService : CurrencyService {

    /**
     * Helper function to apply a random variation (+/- 5%) to a base value.
     */
    private fun randomizeValue(baseValue: Double): Double {
        if (baseValue == 0.0) return 0.0 // Avoid multiplying zero

        // Calculate a random multiplier between 0.95 and 1.05
        // Random.nextDouble(from, until) generates a value in the range [from, until)
        val randomFactor = Random.nextDouble(0.95, 1.05)
        val randomized = baseValue * randomFactor

        // Optional: Format to a reasonable number of decimal places if needed
        // For crypto like SHIB with very small values, be careful with rounding.
        // For simplicity here, we'll return the direct result.
        // Example formatting (use with caution):
        // return String.format("%.8f", randomized).toDouble() // Format to 8 decimal places

        return randomized
    }

    override suspend fun getCurrencies(): List<Currency> {
        delay(500) // simulate network request
        // Use a relatively recent timestamp
        val now = System.currentTimeMillis()
        val lastUpdateTime = now - TimeUnit.MINUTES.toMillis(
            Random.nextInt(2, 15).toLong()
        ) // Add slight random variation to update time too

        // Define base values (makes randomization clearer)
        val baseValues = mapOf(
            "USD" to 1.0, "EUR" to 0.92, "JPY" to 155.50, "GBP" to 0.80, "AUD" to 1.50,
            "CAD" to 1.35, "CHF" to 0.90, "CNY" to 7.25, "INR" to 83.50, "BRL" to 5.10,
            "BTC" to 68500.75, "ETH" to 3550.20, "SOL" to 150.80, "BNB" to 590.00, "XRP" to 0.52,
            "DOGE" to 0.15, "ADA" to 0.45, "AVAX" to 35.50, "SHIB" to 0.000025, "DOT" to 7.10
        )

        return listOf(
            // --- Fiat Currencies ---
            Currency(
                1L,
                "USD",
                "United States Dollar",
                randomizeValue(baseValues["USD"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/US_dollar_symbol.svg/800px-US_dollar_symbol.svg.png",
                true
            ),
            Currency(
                2L,
                "EUR",
                "Euro",
                randomizeValue(baseValues["EUR"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b7/Euro_symbol.svg/800px-Euro_symbol.svg.png",
                true
            ),
            Currency(
                3L,
                "JPY",
                "Japanese Yen",
                randomizeValue(baseValues["JPY"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/Yen_sign.svg/800px-Yen_sign.svg.png",
                true
            ),
            Currency(
                4L,
                "GBP",
                "British Pound Sterling",
                randomizeValue(baseValues["GBP"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9b/Pound_sterling_symbol.svg/800px-Pound_sterling_symbol.svg.png",
                false
            ),
            Currency(
                5L,
                "AUD",
                "Australian Dollar",
                randomizeValue(baseValues["AUD"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Australian_dollar_sign.svg/800px-Australian_dollar_sign.svg.png",
                false
            ),
            Currency(
                6L,
                "CAD",
                "Canadian Dollar",
                randomizeValue(baseValues["CAD"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Canadian_dollar_symbol.svg/800px-Canadian_dollar_symbol.svg.png",
                false
            ),
            Currency(
                7L,
                "CHF",
                "Swiss Franc",
                randomizeValue(baseValues["CHF"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/84/Swiss_franc_sign.svg/800px-Swiss_franc_sign.svg.png",
                false
            ),
            Currency(
                8L,
                "CNY",
                "Chinese Yuan Renminbi",
                randomizeValue(baseValues["CNY"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Yuan_symbol.svg/800px-Yuan_symbol.svg.png",
                false
            ),
            Currency(
                9L,
                "INR",
                "Indian Rupee",
                randomizeValue(baseValues["INR"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Indian_rupee_symbol.svg/800px-Indian_rupee_symbol.svg.png",
                false
            ),
            Currency(
                10L,
                "BRL",
                "Brazilian Real",
                randomizeValue(baseValues["BRL"]!!),
                lastUpdateTime,
                false,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Brazilian_real_symbol.svg/800px-Brazilian_real_symbol.svg.png",
                false
            ),

            // --- Cryptocurrencies ---
            Currency(
                11L,
                "BTC",
                "Bitcoin",
                randomizeValue(baseValues["BTC"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/Bitcoin.svg/800px-Bitcoin.svg.png",
                true
            ),
            Currency(
                12L,

                "ETH",
                "Ethereum",
                randomizeValue(baseValues["ETH"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Ethereum_logo_2014.svg/800px-Ethereum_logo_2014.svg.png",
                true
            ),
            Currency(
                13L,
                "SOL",
                "Solana",
                randomizeValue(baseValues["SOL"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Solana_logo.svg/800px-Solana_logo.svg.png",
                false
            ),
            Currency(
                14L,
                "BNB",
                "Binance Coin",
                randomizeValue(baseValues["BNB"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Binance_Logo.svg/800px-Binance_Logo.svg.png",
                false
            ),
            Currency(
                15L,
                "XRP",
                "Ripple",
                randomizeValue(baseValues["XRP"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1d/XRP_Logo.svg/800px-XRP_Logo.svg.png",
                false
            ),
            Currency(
                16L,
                "DOGE",
                "Dogecoin",
                randomizeValue(baseValues["DOGE"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c4/Dogecoin_logo.svg/800px-Dogecoin_logo.svg.png",
                false
            ),
            Currency(
                17L,
                "ADA",
                "Cardano",
                randomizeValue(baseValues["ADA"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/Cardano_logo.svg/800px-Cardano_logo.svg.png",
                false
            ),
            Currency(
                18L,
                "AVAX",
                "Avalanche",
                randomizeValue(baseValues["AVAX"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Avalanche_logo.svg/800px-Avalanche_logo.svg.png",
                false
            ),
            Currency(
                19L,
                "SHIB",
                "Shiba Inu",
                randomizeValue(baseValues["SHIB"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Shiba_Inu_logo.svg/800px-Shiba_Inu_logo.svg.png",
                false
            ),
            Currency(
                20L,
                "DOT",
                "Polkadot",
                randomizeValue(baseValues["DOT"]!!),
                lastUpdateTime,
                true,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Polkadot_logo.svg/800px-Polkadot_logo.svg.png",
                false
            )

        )
    }

    override suspend fun getCurrenciesRates(): List<CurrencyRateRecord> {
        delay(500) // simulate network request
        val currencies = getCurrencies()
        val currencyRates = mutableListOf<CurrencyRateRecord>()
        val now = System.currentTimeMillis()

        currencies.forEach { currency ->
            currencyRates.add(
                CurrencyRateRecord(
                    id = currency.id, // Use the same ID as the currency for simplicity in this fake service
                    currencyId = currency.id,
                    lastKnownValue = randomizeValue(currency.lastKnownValue), // Apply slight random variation
                    lastUpdated = now - TimeUnit.SECONDS.toMillis(
                        Random.nextInt(5, 60).toLong()
                    ) // Simulate a recent update
                )
            )
        }
        return currencyRates
    }
}