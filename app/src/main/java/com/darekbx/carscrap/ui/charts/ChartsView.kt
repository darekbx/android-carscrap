package com.darekbx.carscrap.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.carscrap.domain.ChartData
import com.darekbx.carscrap.repository.local.dto.CarModel
import com.darekbx.carscrap.ui.theme.CarScrapTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChartsView(filterId: String = "", chartsViewModel: ChartsViewModel = koinViewModel()) {
    val years by chartsViewModel.years
    val chartData by chartsViewModel.chartData

    LaunchedEffect(Unit) {
        chartsViewModel.fetchYears(filterId)
        chartsViewModel.fetchChartData(filterId)
    }

    when {
        chartData.isNotEmpty() -> Chart(Modifier.fillMaxSize(), years, chartData)
        else -> ProgressView()
    }
}

@Composable
fun ProgressView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.size(64.dp))
    }
}

@Composable
fun Chart(modifier: Modifier, years: List<Int>, chartData: List<ChartData>) {
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var drawLines by remember { mutableStateOf(false) }

    Column(
        modifier
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            )
    ) {
        PriceChart(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
                .weight(1F),
            chartData = chartData,
            selectedYear = selectedYear,
            drawLines = drawLines
        )

        YearSelection(years, onSelectedYear = { selectedYear = it })

        Row(
            modifier = Modifier.padding(top = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 38.dp) {
                Checkbox(
                    checked = drawLines,
                    onCheckedChange = { drawLines = it },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            Text(
                text = "Draw lines",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = "Filters (0)",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                modifier = Modifier.clickable { /* TODO */ },
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun YearSelection(
    years: List<Int>,
    onSelectedYear: (Int?) -> Unit
) {
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        years.forEach { year ->
            val decoration =
                if (selectedYear != year) TextDecoration.Underline
                else TextDecoration.None
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        onSelectedYear(year)
                        selectedYear = year
                    },
                text = "$year",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = if (selectedYear == year) FontWeight.Bold else FontWeight.Normal,
                style = TextStyle(textDecoration = decoration)
            )
        }
        val decoration =
            if (selectedYear != null) TextDecoration.Underline
            else TextDecoration.None
        Text(
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    onSelectedYear(null)
                    selectedYear = null
                },
            text = "All years",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = if (selectedYear == null) FontWeight.Bold else FontWeight.Normal,
            style = TextStyle(textDecoration = decoration)
        )
    }
}

@Composable
fun PriceChart(
    modifier: Modifier,
    chartData: List<ChartData>,
    selectedYear: Int? = null,
    drawLines: Boolean = true
) {
    val measurer = rememberTextMeasurer()
    val circleBgColor = MaterialTheme.colorScheme.surfaceContainer
    val allCars = chartData.flatMap { it.carModels }
    val distinctPrices = getDistinctFullPrices(allCars.map { it.price })
    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    val maxValue = allCars.maxOf { it.price }
    val minValue = allCars.minOf { it.price }

    Canvas(modifier = modifier
        .padding(8.dp)
        .onGloballyPositioned { viewSize = it.size }
    ) {
        val rightOffset = 70F
        val height = viewSize.height.toFloat()
        val width = viewSize.width.toFloat() - rightOffset
        val hRatio = height / (maxValue - minValue)

        drawGuideLines(distinctPrices, height, minValue, hRatio, viewSize, width, measurer)

        chartData.forEachIndexed { i, carsForYear ->
            val items = carsForYear.carModels
            val wRatio = width / (items.size - 1)

            val color = when (selectedYear) {
                null -> colors[i]
                carsForYear.year -> colors[i]
                else -> colors[i].copy(alpha = 0.2F)
            }

            val focusChart = selectedYear != null && selectedYear == carsForYear.year
            val isYearFocused = selectedYear != null && selectedYear != carsForYear.year

            if (drawLines) {
                drawChart(items, wRatio, height, minValue, hRatio, color, focusChart)
            }

            val bgColor = if (drawLines) circleBgColor else color
            drawCircles(items, wRatio, height, minValue, hRatio, bgColor, color, isYearFocused)

            if (selectedYear == carsForYear.year) {
                drawTrendLine(items, wRatio, height, minValue, hRatio, bgColor)
            }
        }
    }
}

private fun DrawScope.drawTrendLine(
    items: List<CarModel>,
    wRatio: Float,
    height: Float,
    minValue: Int,
    hRatio: Float,
    bgColor: Color
) {
    calculatePriceTrendLine(items).let { (start, end) ->
        val sx = start.x * wRatio
        val ex = end.x * wRatio
        val sy = height - (start.y - minValue) * hRatio
        val ey = height - (end.y - minValue) * hRatio

        drawLine(
            color = Color.White,
            start = Offset(sx, sy),
            end = Offset(ex, ey),
            strokeWidth = 4f
        )

        drawCircle(bgColor, center = Offset(sx, sy), radius = 5f)
        drawCircle(Color.White, center = Offset(sx, sy), radius = 5f, style = Stroke(width = 2f))

        drawCircle(bgColor, center = Offset(ex, ey), radius = 5f)
        drawCircle(Color.White, center = Offset(ex, ey), radius = 5f, style = Stroke(width = 2f))
    }
}

private fun DrawScope.drawGuideLines(
    distinctFullPrices: Set<Int>,
    height: Float,
    minValue: Int,
    heightRatio: Float,
    containerSize: IntSize,
    width: Float,
    textMeasurer: TextMeasurer
) {
    drawLine(
        color = Color.DarkGray,
        start = Offset(0f, 0f),
        end = Offset(0f, height),
        strokeWidth = 1f
    )
    distinctFullPrices.forEach { price ->
        val y = height - (price - minValue) * heightRatio
        if (y > 0 && y < containerSize.height) {
            drawLine(
                color = Color.DarkGray,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            drawText(
                textMeasurer = textMeasurer,
                text = formatToK(price),
                topLeft = Offset(width + 8F, y - 16F),
                style = TextStyle(fontSize = 10.sp, color = Color.Gray)
            )
        }
    }
}

private fun DrawScope.drawChart(
    cars: List<CarModel>,
    widthRatio: Float,
    height: Float,
    minValue: Int,
    heightRatio: Float,
    chartColor: Color,
    focusChart: Boolean
) {
    var firstPoint = Offset(0f, height - (cars.first().price - minValue) * heightRatio)
    cars.forEachIndexed { index, carModel ->
        val x = index * widthRatio
        val y = height - (carModel.price - minValue) * heightRatio

        drawLine(
            color = chartColor,
            start = firstPoint,
            end = Offset(x, y),
            strokeWidth = if (focusChart) 4f else 2f
        )

        firstPoint = Offset(x, y)
    }
}

private fun DrawScope.drawCircles(
    cars: List<CarModel>,
    widthRatio: Float,
    height: Float,
    minValue: Int,
    heightRatio: Float,
    bgColor: Color,
    chartColor: Color,
    isYearFocused: Boolean
) {
    cars.forEachIndexed { index, carModel ->
        val x = index * widthRatio
        val y = height - (carModel.price - minValue) * heightRatio
        if (!isYearFocused) {
            drawCircle(
                color = bgColor,
                center = Offset(x, y),
                radius = 5f,
            )
        }
        drawCircle(
            color = chartColor,
            center = Offset(x, y),
            radius = 5f,
            style = Stroke(width = 2f)
        )
    }
}

private fun calculatePriceTrendLine(carModels: List<CarModel>): Pair<Offset, Offset> {
    val n = carModels.size
    var sumX = 0.0
    var sumY = 0.0
    var sumXY = 0.0
    var sumXX = 0.0

    carModels.forEachIndexed { index, car ->
        val x = index.toDouble()
        val y = car.price.toDouble()

        sumX += x
        sumY += y
        sumXY += x * y
        sumXX += x * x
    }

    val slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
    val yIntercept = (sumY - slope * sumX) / n
    val startX = 0.0F
    val endX = (n - 1).toFloat()
    val startY = (slope * startX + yIntercept).toFloat()
    val endY = (slope * endX + yIntercept).toFloat()

    return Pair(
        Offset(startX, startY),
        Offset(endX, endY)
    )
}

private fun getDistinctFullPrices(prices: List<Int>): Set<Int> {
    return prices.map { (it / 10000) * 10000 }.toSet()
}

private fun formatToK(value: Int): String {
    return "${value / 1000}k"
}

val colors = listOf(
    Color(0xFF1ABC9C),
    Color(0xFF2ECC71),
    Color(0xFF3498DB),
    Color(0xFF9B59B6),
    Color(0xFFE74C3C),
    Color(0xFFF39C12),
    Color(0xFFC0392B),
    Color(0xFF8E44AD),
    Color(0xFF2980B9),
    Color(0xFF27AE60),
    Color(0xFFB71C1C),
    Color(0xFF0D47A1),
    Color(0xFF2E7D32),
    Color(0xFFF57F17)
)


@Preview
@Composable
fun ChartPreview() {
    CarScrapTheme {
        Box(
            Modifier
                .padding(8.dp)
                .size(200.dp, 100.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    MaterialTheme.shapes.medium
                )
        ) {
            PriceChart(
                modifier = Modifier.fillMaxSize(),
                listOf(
                    ChartData(
                        2015,
                        listOf(
                            52000, 48000, 49000, 44000, 55000,
                            59000, 62000, 61000, 63000, 60000,
                            60000, 59000, 55000, 57000, 56000
                        ).map { priceModel(it) }
                    ),
                    ChartData(
                        2017,
                        listOf(
                            58000, 59000, 56000, 69000, 65000,
                            69000, 67000, 65000, 75000, 68000,
                            72000, 68000, 67000, 61000, 113000
                        ).map { priceModel(it) }
                    )
                ),
                selectedYear = null
            )
        }
    }
}

private fun priceModel(price: Int) = CarModel("", "", 0L, "", price, "", "", "", 0, 0, "", 0, "")
