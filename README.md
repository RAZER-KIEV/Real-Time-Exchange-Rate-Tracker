# Mobile Engineer Assignment: Real-Time Exchange Rate Tracker

## Objective

Create a mobile application that displays real-time exchange rates for a list of selected currencies or assets. Users should be able to add and remove currencies/assets from their personalized board. The app should update prices automatically in near real-time.

## Requirements

### Functional

1.  **Home Screen:** Displays a list of selected assets (e.g., USD, EUR, BTC) and their current exchange rates.
    * A fake dataset of 20 assets was generated.
    * Exchange rates auto-update. A fake repository simulates API updates with randomized values since free APIs didn't support 3-5 second updates.
      ![Home screen](screenshots/exchange_rates.png)
2.  **Add Asset Screen:** A searchable screen to browse and add available currencies/assets to the board.
    * Implemented selection of multiple currencies at once, sorted by whether they are cryptocurrencies.
    * Only assets not already on the board are shown.
    * Selected assets persist between sessions using Room DB. Data is backed by the database, following the "Single source of truth" pattern.
      ![Add asset screen](screenshots/add_asset_selected_few_curr.png)
    * Search functionality also implemented.
      ![Add asset screen with search](screenshots/add_asset_with_search.png)
3.  **Remove Asset:** Users can remove assets from their list. Removed assets become available again on the "Add Asset" screen.

### Data Source

* A mock API was created to emulate the required 3-5 second refresh rate behavior, as publicly available free APIs researched (e.g., OpenExchangeRates, ExchangeRate.host, CoinGecko, ECB rates) did not offer this frequency.

### Tech Stack

* **Language/Framework:** Kotlin with Jetpack Compose.
* **Architecture:** MVVM.
* **Database:** Room DB for persistence and offline support.
* **Backend:** None required; client-only solution.

### Bonus Points Implemented

* **Animations:** "Swipe to remove" mechanism implemented.
* **Error Handling:** Proper error handling for simulated API issues, displaying Toast messages with details.
* **Testing:** Basic unit/UI testing included.
* **Offline Support:** Last known rates are available offline due to Room DB integration.

## Setup Instructions

* The API is mocked within the application. No external API setup is needed.

## Assumptions Made

* Due to limitations with free real-time data sources providing 3-5 second updates, a fake repository was implemented to simulate this behavior.

## Submission

* Code uploaded to GitHub.
* Screenshots added.

## Estimated Time vs. Actual Time

* **Estimated:** 4–6 hours.
* **Actual:** Approximately 12 hours.

