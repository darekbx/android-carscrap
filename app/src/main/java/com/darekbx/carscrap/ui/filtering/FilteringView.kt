package com.darekbx.carscrap.ui.filtering

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun FilteringView(
    filterId: String,
    onApply: (FilterInfo) -> Unit,
    filteringViewModel: FilteringViewModel = koinViewModel()
) {
    var filterInfo by remember { mutableStateOf(FilterInfo()) }
    val fuelTypes by filteringViewModel.fuelTypes
    val enginePowers by filteringViewModel.enginePowers
    val gearboxes by filteringViewModel.gearboxes
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        filteringViewModel.fetchFilters(filterId)
        filteringViewModel.fetchFilterInfo(filterId)?.let {
            filterInfo = it
        }
        isLoading = false
    }

    Box(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        } else {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = "Filters",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
                    )

                    MultiSelectDropdown(
                        items = fuelTypes.map { AnnotatedItem(it) },
                        label = "Fuel types",
                        selected = filterInfo.fuelType,
                        onSelectionChanged = { items -> filterInfo.addFuelTypes(items) }
                    )

                    MultiSelectDropdown(
                        items = enginePowers.map {
                            AnnotatedItem(
                                it.enginePower.toString(),
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(it.enginePower.toString())
                                    }
                                    withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                                        append(" (${it.count})")
                                    }
                                }
                            )
                        },
                        label = "Engine powers",
                        selected = filterInfo.enginePower.map { it.toString() },
                        onSelectionChanged = { items -> filterInfo.addEnginePowers(items.map { it.toInt() }) }
                    )

                    MultiSelectDropdown(
                        items = gearboxes.map { AnnotatedItem(it) },
                        label = "Gearboxes",
                        selected = filterInfo.gearbox,
                        onSelectionChanged = { items -> filterInfo.addGearboxes(items) }
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = {
                        filteringViewModel.saveFilterInfo(filterId, filterInfo) {
                            onApply(filterInfo)
                        }
                    }) {
                    Text(text = "Apply filters")
                }
            }
        }
    }
}