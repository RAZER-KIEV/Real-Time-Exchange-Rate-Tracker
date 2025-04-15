package com.example.real_timeexchangeratetracker.ui.add_asset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.real_timeexchangeratetracker.data.model.Currency
import com.example.real_timeexchangeratetracker.domain.CurrencyViewModel
import com.example.real_timeexchangeratetracker.ui.home.CurrencyItem
import com.example.real_timeexchangeratetracker.ui.theme.RealTimeExchangeRateTrackerTheme
import kotlinx.serialization.Serializable

@Composable
fun SearchableScreen(onDoneClicked: () -> Unit, viewModel: CurrencyViewModel) {
    val allCurr = viewModel.allCurrencies.collectAsStateWithLifecycle()
    val searched = viewModel.searchedCurrencies.collectAsStateWithLifecycle()

    val operationalList = searched.value.ifEmpty {
        allCurr.value

    }
    SearchableScreen(
        onDoneClicked = onDoneClicked,
        operationalList = operationalList,
        onSearchQueryChange = { newValue: String -> viewModel.onSearchQueryChange(newValue) },
        onClearSearch = { viewModel.onClearSearch() },
        updateCurrency = { currency -> viewModel.updateCurrency(currency) }
    )
}

@Composable
fun SearchableScreen(
    onDoneClicked: () -> Unit,
    operationalList: List<Currency>,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    updateCurrency: (Currency) -> Unit,
) {
    val currFiltered = operationalList.filter { currency -> !currency.isMonitored }
    val assets by remember(currFiltered) {
        derivedStateOf {
            currFiltered.filter { currency -> !currency.isCrypto }
        }
    }
    val cryptos by remember(currFiltered) {
        derivedStateOf {
            currFiltered.filter { currency -> currency.isCrypto }
        }
    }

    var searchText by remember { mutableStateOf("") }
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
        topBar = {
            CustomTwoRowTopBar(
                modifier = Modifier,
                searchText = searchText,
                onSearchTextChange = {
                    onSearchQueryChange(it)
                    searchText = it
                },
                onBackClick = onDoneClicked,
                onDoneClick = onDoneClicked,
                onClearSearchClick = {
                    searchText = ""
                    onClearSearch()
                })
        }
    ) { innerPadding ->
        LazyList(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            updateCurrency = updateCurrency,
            assets = assets,
            cryptos = cryptos
        )
    }
}

@Composable
fun LazyList(
    modifier: Modifier = Modifier,
    updateCurrency: (Currency) -> Unit,
    assets: List<Currency>,
    cryptos: List<Currency>
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 8.dp)) {
        item {
            Text(modifier = Modifier.padding(16.dp), text = "Popular Assets:")
        }

        items(
            items = assets,
            key = { currency -> currency.id }
        ) { currency ->
            CurrencyItem(
                currency = currency,
                actionView = {
                    CheckBoxHolder(currency) {
                        updateCurrency(currency.copy(isMonitored = it))
                    }
                }
            )
        }

        item {
            Text(modifier = Modifier.padding(16.dp), text = "Cryptocurrencies:")
        }

        items(
            items = cryptos,
            key = { currency -> currency.id }
        ) { currency ->
            CurrencyItem(
                currency = currency,
                actionView = {
                    CheckBoxHolder(currency) {
                        updateCurrency(currency.copy(isMonitored = it))
                    }
                }
            )
        }
    }
}

@Composable
fun CheckBoxHolder(currency: Currency, onCheckedChanged: (Boolean) -> Unit) {
    var checkedState by rememberSaveable { mutableStateOf(currency.isMonitored) }
    Checkbox(checked = checkedState, onCheckedChange = {
        checkedState = it
        onCheckedChanged(it)
    })
}

@Composable
fun CustomTwoRowTopBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    onClearSearchClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Add asset",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            TextButton(onClick = onDoneClick) {
                Text("Done")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = onClearSearchClick) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.extraLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomTwoRowTopBarPreview() {
    RealTimeExchangeRateTrackerTheme {
        CustomTwoRowTopBar(
            searchText = "Sample search",
            onSearchTextChange = {},
            onBackClick = {},
            onDoneClick = {},
            onClearSearchClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomTwoRowTopBarEmptySearchPreview() {
    RealTimeExchangeRateTrackerTheme {
        CustomTwoRowTopBar(
            searchText = "",
            onSearchTextChange = {},
            onBackClick = {},
            onDoneClick = {},
            onClearSearchClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchableScreenPreview() {
    RealTimeExchangeRateTrackerTheme {
        SearchableScreen({}, emptyList(), {}, {}, {})
    }
}

@Serializable
object SearchableScreenRoute