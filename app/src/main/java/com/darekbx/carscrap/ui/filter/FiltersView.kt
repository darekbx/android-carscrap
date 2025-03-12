package com.darekbx.carscrap.ui.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.carscrap.ui.theme.CarScrapTheme
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Popup
import com.darekbx.carscrap.repository.local.dto.Filter
import java.util.UUID

@Composable
fun FiltersView(
    filterViewModel: FilterViewModel = koinViewModel(),
    onFilterSelected: (String) -> Unit,
    onAddNewFilter: () -> Unit
) {
    val filters by filterViewModel.filters
    val inProgress by filterViewModel.inProgress

    LaunchedEffect(key1 = Unit) {
        filterViewModel.fetchFilters()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (inProgress) {
            ProgressBox()
        } else {
            filters?.let { filtersList ->
                LazyColumn {
                    items(filtersList) { filter ->
                        FilterItem(
                            filter = filter,
                            onClick = { filterId -> onFilterSelected(filterId) }
                        )
                    }
                    item { MazdaFilter { onFilterSelected("") } }
                    item { AddNewButton { onAddNewFilter() } }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No filters available")
                }
            }
        }
    }
}

@Composable
private fun ProgressBox() {
    Popup {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
        ) {
            CircularProgressIndicator(
                Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        }
    }
}

@Composable
private fun AddNewButton(onClick: () -> Unit = { }) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add new filter",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Add new filter",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun MazdaFilter(onClick: () -> Unit = { }) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mazda 6 (Firebase)",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun FilterItem(
    filter: Filter,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(filter.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = filter.make.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = filter.model.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                    if (filter.generation.isNotBlank()) {
                        Text(
                            text = "(${filter.generation})",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
                Text(
                    text = filter.id,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                Text(text = "Salvage ", style = MaterialTheme.typography.bodyMedium)
                Checkbox(
                    checked = filter.salvage,
                    onCheckedChange = null, // Read-only checkbox
                    enabled = false
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FiltersViewPreview() {
    val sampleFilters = listOf(
        Filter(
            id = UUID.randomUUID().toString(),
            make = "toyota",
            model = "camry",
            generation = "XV70",
            salvage = false
        ),
        Filter(
            id = UUID.randomUUID().toString(),
            make = "honda",
            model = "civic",
            generation = "",
            salvage = true
        )
    )
    CarScrapTheme {
        Surface {
            Column {
                sampleFilters.forEach { filter ->
                    FilterItem(
                        filter = filter,
                        onClick = {}
                    )
                }
                AddNewButton()
            }
        }
    }
}
