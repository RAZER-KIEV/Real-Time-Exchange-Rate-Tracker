package com.example.real_timeexchangeratetracker.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.real_timeexchangeratetracker.R
import com.example.real_timeexchangeratetracker.data.model.Currency
import com.example.real_timeexchangeratetracker.domain.CurrencyViewModel
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onAddClicked: () -> Unit, viewModel: CurrencyViewModel) {
    val monitoredCur = viewModel.monitoredCurrencies.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startMonitoringRatesIfNeeded()
        Log.d("HomeScreen", "LaunchedEffect triggered: Attempting to start monitoring if needed.")
    }

    Scaffold(Modifier.background(Color.Blue),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exchange_rates)) },
                actions = {
                    IconButton(
                        onClick = { onAddClicked() },
                        modifier = Modifier
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.LightGray, CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { paddingValues ->
        RemovableCurrencyList(
            modifier = Modifier.padding(paddingValues),
            currencies = monitoredCur.value,
            viewModel = viewModel
        )

    }
}

@Composable
fun BottomNavigationBar() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        NavigationItem(
            title = stringResource(R.string.home),
            icon = Icons.Filled.Home
        ),
        NavigationItem(
            title = stringResource(R.string.favorites),
            icon = Icons.Filled.Favorite
        ),
        NavigationItem(
            title = stringResource(R.string.markets),
            icon = Icons.Filled.ShoppingCart
        ),
        NavigationItem(
            title = stringResource(R.string.settings),
            icon = Icons.Filled.Settings
        )
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CurrencyItem(
    currency: Currency,
    actionView: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                modifier = Modifier
                    .padding(8.dp)
                    .size(48.dp),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
                model = currency.iconLink,
                contentDescription = "Remove ${currency.shortName}",
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${currency.longName} (${currency.shortName})",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Value: ${
                        String.format(
                            "%.4f",
                            currency.lastKnownValue
                        )
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            actionView()
        }
    }
}

@Composable
fun DeleteButton(onRemoveClick: () -> Unit) {
    Box {
        IconButton(
            onClick = { onRemoveClick() },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.remove),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun RemovableCurrencyList(
    viewModel: CurrencyViewModel,
    currencies: List<Currency>,
    modifier: Modifier = Modifier
) {
    RemovableCurrencyList(
        updateCurrency = { currency: Currency -> viewModel.updateCurrency(currency) },
        currencies.toMutableList(),
        modifier
    )
}

@Composable
fun RemovableCurrencyList(
    updateCurrency: (Currency) -> Unit,
    currencies: MutableList<Currency>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {

        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.last_updated_just_now)
            )
        }
        items(
            items = currencies,
            key = { currency -> currency.id }
        ) { currency ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue != SwipeToDismissBoxValue.Settled) {
                        val copied = currency.copy(isMonitored = false)
                        updateCurrency(copied)
                        currencies.remove(currency)
                        true
                    } else {
                        false
                    }
                },
                positionalThreshold = { totalDistance -> totalDistance * 0.2f }
            )
            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = { },
                content = {
                    CurrencyItem(
                        currency = currency,
                        actionView = { }
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyItemPreview() {
    val sampleCurrency = Currency(
        id = 1L,
        shortName = "BTC",
        longName = "Bitcoin",
        lastKnownValue = 65432.10,
        lastUpdated = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2),
        isCrypto = true,
        iconLink = "https://picsum.photos/200/300",
        isMonitored = true
    )
    MaterialTheme {
        CurrencyItem(
            currency = sampleCurrency,
            actionView = {
                DeleteButton(
                    onRemoveClick = { },
                )
            },
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun RemovableCurrencyListPreview() {
    val sampleList = remember { getSampleCurrencies() }
    MaterialTheme {
        RemovableCurrencyList(currencies = sampleList, updateCurrency = {})
    }
}

// Sample Data Function (for previews and initial state)
fun getSampleCurrencies(): SnapshotStateList<Currency> {
    return mutableStateListOf(
        Currency(
            1L,
            "USD",
            "US Dollar",
            1.0,
            System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5),
            false,
            iconLink = "https://picsum.photos/200/300",
            true
        ),
        Currency(
            2L,
            "EUR",
            "Euro",
            0.92,
            System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10),
            false,
            iconLink = "https://picsum.photos/200/300",
            true
        ),
        Currency(
            3L,
            "BTC",
            "Bitcoin",
            65432.10,
            System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1),
            true,
            iconLink = "https://picsum.photos/200/300",
            true
        ),
        Currency(
            4L,
            "ETH",
            "Ethereum",
            3450.55,
            System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30),
            true,
            iconLink = "https://picsum.photos/200/300",
            true
        ),
        Currency(
            5L,
            "JPY",
            "Japanese Yen",
            155.3,
            System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2),
            false,
            iconLink = "https://picsum.photos/200/300",
            true
        )
    )
}

data class NavigationItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Serializable
object HomeScreenRoute
