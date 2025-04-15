package com.example.real_timeexchangeratetracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.real_timeexchangeratetracker.domain.CurrencyViewModel
import com.example.real_timeexchangeratetracker.ui.add_asset.SearchableScreen
import com.example.real_timeexchangeratetracker.ui.add_asset.SearchableScreenRoute
import com.example.real_timeexchangeratetracker.ui.home.HomeScreen
import com.example.real_timeexchangeratetracker.ui.home.HomeScreenRoute
import com.example.real_timeexchangeratetracker.ui.theme.RealTimeExchangeRateTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val currencyViewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RealTimeExchangeRateTrackerTheme {
                MyAppNavigation(currencyViewModel = currencyViewModel)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currencyViewModel.errorFlow.collect { errorMessage ->
                    if (errorMessage.isNotEmpty()) {
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, currencyViewModel: CurrencyViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeScreenRoute,
        modifier = modifier
    ) {
        composable<HomeScreenRoute> {
            HomeScreen(onAddClicked = {
                navController.navigate(SearchableScreenRoute)
            }, viewModel = currencyViewModel)
        }
        composable<SearchableScreenRoute> {
            SearchableScreen(
                viewModel = currencyViewModel,
                onDoneClicked = { navController.navigateUp() }
            )
        }
    }
}
