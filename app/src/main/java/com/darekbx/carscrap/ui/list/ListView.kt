package com.darekbx.carscrap.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.darekbx.carscrap.repository.local.dto.CarModel
import com.darekbx.carscrap.ui.theme.CarScrapTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darekbx.carscrap.ui.charts.ProgressView
import org.koin.androidx.compose.koinViewModel

data class ColumnDefinition<T>(
    val title: String,
    val weight: Float = 1f,
    val content: @Composable (T) -> Unit,
    val sortFunction: (List<T>) -> List<T> = { emptyList<T>() }
)

enum class SortDirection {
    NONE, ASC, DESC
}

@Composable
fun ListView(filterId: String = "", viewModel: ListViewModel = koinViewModel()) {
    val cars by viewModel.cars
    val inProgress by viewModel.inProgress

    LaunchedEffect(filterId) {
        viewModel.fetchListData(filterId)
    }

    if (inProgress) {
        ProgressView()
        return
    }

    ListView(cars, onDelete = { id ->
        viewModel.delete(id)
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListView(cars: List<CarModel>, onDelete: (String) -> Unit = {}) {
    var activeCarModel by remember { mutableStateOf<CarModel?>(null) }
    val columns = listOf(
        ColumnDefinition<CarModel>(
            title = "Price",
            weight = 1f,
            content = { Text("${it.price}") },
            sortFunction = { it.sortedBy { car -> car.price } }
        ),
        ColumnDefinition(
            title = "Mileage",
            weight = 1f,
            content = { Text(it.mileage.toString()) },
            sortFunction = { it.sortedBy { car -> car.mileage } }
        ),
        ColumnDefinition(
            title = "Power",
            weight = 1f,
            content = { Text("${it.enginePower}") },
            sortFunction = { it.sortedBy { car -> car.enginePower } }
        ),
        ColumnDefinition(
            title = "Year",
            weight = 1f,
            content = { Text("${it.year}") },
            sortFunction = { it.sortedBy { car -> car.year } }
        ),
        ColumnDefinition(
            title = "Country",
            weight = 1f,
            content = { Text(it.countryOrigin) },
            sortFunction = { it.sortedBy { car -> car.countryOrigin } }
        )
    )

    SortableTable(
        data = cars,
        columns = columns,
        modifier = Modifier.fillMaxSize()
    ) {
        activeCarModel = it
    }

    activeCarModel?.let {
        CarModelDialog(
            carModel = it,
            onDismiss = { activeCarModel = null },
            onDelete = {
                activeCarModel?.id?.let {
                    onDelete(it)
                }
                activeCarModel = null
            }
        )
    }
}

@Composable
fun <T> SortableTable(
    data: List<T>,
    columns: List<ColumnDefinition<T>>,
    modifier: Modifier = Modifier,
    onItemClick: (T) -> Unit = {}
) {
    var sortedData by remember { mutableStateOf(data) }
    Column(modifier = modifier) {
        Header(columns, data) { sortedData = it }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        LazyColumn(modifier = Modifier.padding(bottom = 12.dp)) {
            items(sortedData) { item ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                    columns.forEach { column ->
                        Box(
                            modifier = Modifier
                                .weight(column.weight)
                                .padding(8.dp)
                                .clickable(onClick = { onItemClick(item) })
                        ) {
                            column.content(item)
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun <T> Header(
    columns: List<ColumnDefinition<T>>,
    data: List<T>,
    onSortedData: (List<T>) -> Unit = {}
) {
    var currentSortedColumn by remember { mutableStateOf<String?>(null) }
    var currentSortDirection by remember { mutableStateOf(SortDirection.NONE) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(vertical = 8.dp)
    ) {
        columns.forEach { column ->
            fun applySorting() {
                currentSortDirection = when {
                    currentSortedColumn != column.title -> SortDirection.ASC
                    currentSortDirection == SortDirection.ASC -> SortDirection.DESC
                    else -> SortDirection.NONE
                }
                currentSortedColumn =
                    if (currentSortDirection == SortDirection.NONE) {
                        onSortedData(data)
                        null
                    } else {
                        onSortedData(
                            when (currentSortDirection) {
                                SortDirection.ASC -> column.sortFunction(data)
                                SortDirection.DESC -> column.sortFunction(data).reversed()
                                SortDirection.NONE -> data
                            }
                        )
                        column.title
                    }
            }
            Box(
                modifier = Modifier
                    .weight(column.weight)
                    .clickable { applySorting() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                SortItem(column, currentSortedColumn, currentSortDirection)
            }
        }
    }
}

@Composable
private fun <T> SortItem(
    column: ColumnDefinition<T>,
    currentSortedColumn: String?,
    currentSortDirection: SortDirection
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = column.title,
            fontWeight = FontWeight.Bold,
            color = if (currentSortedColumn == column.title)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (currentSortedColumn == column.title) {
            val icon = when (currentSortDirection) {
                SortDirection.ASC -> Icons.Default.KeyboardArrowDown
                SortDirection.DESC -> Icons.Default.KeyboardArrowUp
                else -> throw IllegalStateException("Sort direction not set")
            }
            Icon(
                imageVector = icon,
                contentDescription = "Sort direction",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun ListViewPreview(@PreviewParameter(CarModelListProvider::class) items: List<CarModel>) {
    CarScrapTheme {
        ListView(items)
    }
}

class CarModelListProvider : PreviewParameterProvider<List<CarModel>> {
    override val values: Sequence<List<CarModel>> = sequenceOf(
        listOf(
            PreviewCarModel(10000, 2010, 155000).create(),
            PreviewCarModel(30000, 2012, 120000).create(),
            PreviewCarModel(40000, 2014, 202000).create(),
            PreviewCarModel(50000, 2016, 195000).create(),
            PreviewCarModel(60000, 2011, 241000).create(),
            PreviewCarModel(45000, 2013, 334000).create(),
            PreviewCarModel(35000, 2015, 221000).create(),
            PreviewCarModel(22000, 2016, 153000).create(),
        )
    )
}

data class PreviewCarModel(
    val price: Int,
    val year: Int,
    val mileage: Int,
) {
    fun create() = CarModel(
        externalId = "",
        createdAt = System.currentTimeMillis(),
        url = "https://www.otomoto.pl/osobowe/oferta/toyota-corolla-toyota-corolla-1-8-ts-hybrid-comfort-tech-salon-pl-1-wl-fv-23-ID6H9hxS.html",
        price = price,
        currency = "PLN",
        fuelType = "petrol",
        gearbox = "manual",
        enginePower = 165,
        year = year,
        countryOrigin = "Poland",
        mileage = mileage,
        filterId = "0"
    )
}
