package com.darekbx.carscrap.ui.filter

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Popup
import com.darekbx.carscrap.repository.local.dto.Filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
fun FiltersView(
    filterViewModel: FilterViewModel = koinViewModel(),
    onFilterSelected: (String) -> Unit,
    onAddNewFilter: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val filters by filterViewModel.filters
    val inProgress by filterViewModel.inProgress
    val refresh by filterViewModel.refresh
    var refreshFilterId by remember { mutableStateOf<String?>(null) }
    var deleteFilter by remember { mutableStateOf<Filter?>(null) }
    var progress = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(refresh) {
        filterViewModel.fetchFilters()
    }

    LaunchedEffect(refreshFilterId) {
        refreshFilterId?.let { filterId ->
            filterViewModel.scrap(
                filterId,
                onProgress = { currentOffset, totalCount ->
                    progress.value = currentOffset.toFloat() / totalCount.toFloat()
                },
                onCompleted = { addedCount ->
                    refreshFilterId = null
                    scope.launch {
                        withContext(Dispatchers.Main) {
                            Toast
                                .makeText(context, "Added $addedCount new cars", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (inProgress && refreshFilterId == null) {
            ProgressBox()
        } else {
            filters?.let { filtersList ->
                LazyColumn {
                    items(filtersList) { filter ->
                        FilterItem(
                            filter = filter,
                            onClick = { filterId -> onFilterSelected(filterId) },
                            onRefresh = { refreshFilterId = filter.id },
                            onDelete = { deleteFilter = filter }
                        )
                    }
                    item { AddNewButton { onAddNewFilter() } }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No filters available")
                }
            }
        }
    }

    refreshFilterId?.let { filterId ->
        ProgressBox(progress)
    }

    deleteFilter?.let { filter ->
        if (filter.itemsCount > 0) {
            DeleteConfirmationDialog(
                message = "Are you sure you want to delete \"${filter.make} ${filter.model}\" filter data?",
                onDismiss = { deleteFilter = null },
                onConfirm = {
                    filterViewModel.deleteFilterData(filter.id)
                    deleteFilter = null
                }
            )
        } else {
            DeleteConfirmationDialog(
                message = "Are you sure you want to delete \"${filter.make} ${filter.model}\" filter?",
                onDismiss = { deleteFilter = null },
                onConfirm = {
                    filterViewModel.deleteFilter(filter.id)
                    deleteFilter = null
                }
            )
        }
    }
}

@Composable
private fun ProgressBox(progress: MutableState<Float>? = null) {
    Popup {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
        ) {
            progress?.let {
                CircularProgressIndicator(
                    progress = { it.value },
                    Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            } ?: run {
                CircularProgressIndicator(
                    Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }
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
private fun FilterItem(
    filter: Filter,
    onClick: (String) -> Unit,
    onRefresh: () -> Unit = { },
    onDelete: () -> Unit = { }
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
                Text(
                    text = buildAnnotatedString {
                        append("Items count: ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(filter.itemsCount.toString())
                        }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
                    .clickable { onDelete() }
            )
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
                    .clickable { onRefresh() }
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
