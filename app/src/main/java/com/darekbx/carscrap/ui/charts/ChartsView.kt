package com.darekbx.carscrap.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.carscrap.domain.ChartData
import com.darekbx.carscrap.repository.local.dto.CarModel
import com.darekbx.carscrap.ui.theme.CarScrapTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChartsView(chartsViewModel: ChartsViewModel = koinViewModel()) {
    val years by chartsViewModel.years
    val chartData by chartsViewModel.chartData

    LaunchedEffect(Unit) {
        chartsViewModel.fetchYears()
        chartsViewModel.fetchChartData()
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
    Box(
        modifier
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            )
    ) {
        PriceChart(Modifier.fillMaxSize(), chartData/*, selectedYear = 2015*/)
    }
}

@Composable
fun PriceChart(modifier: Modifier, chartData: List<ChartData>, selectedYear: Int? = null) {
    val measurer = rememberTextMeasurer()
    val bgColor = MaterialTheme.colorScheme.surfaceContainer
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
        val heightRatio = height / (maxValue - minValue)

        // draw guide lines
        drawGuideLines(distinctPrices, height, minValue, heightRatio, viewSize, width, measurer)

        selectedYear?.let { year ->
            val items = chartData.first { it.year == year }.carModels
            val widthRatio = width / (items.size - 1)
            drawChart(items, widthRatio, height, minValue, heightRatio, colors[0])
            drawCircles(items, widthRatio, height, minValue, heightRatio, bgColor, colors[0])
        } ?: run {
            chartData.forEachIndexed { i, carsForYear ->
                val items = carsForYear.carModels
                val widthRatio = width / (items.size - 1)
                drawChart(items, widthRatio, height, minValue, heightRatio, colors[i])
                drawCircles(items, widthRatio, height, minValue, heightRatio, bgColor, colors[i])
            }
        }
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
    chartColor: Color
) {
    var firstPoint = Offset(0f, height - (cars.first().price - minValue) * heightRatio)
    cars.forEachIndexed { index, carModel ->
        val x = index * widthRatio
        val y = height - (carModel.price - minValue) * heightRatio

        drawLine(
            color = chartColor,
            start = firstPoint,
            end = Offset(x, y),
            strokeWidth = 2f
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
    chartColor: Color
) {
    cars.forEachIndexed { index, carModel ->
        val x = index * widthRatio
        val y = height - (carModel.price - minValue) * heightRatio
        drawCircle(
            color = bgColor,
            center = Offset(x, y),
            radius = 5f,
        )
        drawCircle(
            color = chartColor,
            center = Offset(x, y),
            radius = 5f,
            style = Stroke(width = 2f)
        )
    }
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
    Color(0xFFD35400),
    Color(0xFFC0392B),
    Color(0xFF8E44AD),
    Color(0xFF2980B9),
    Color(0xFF27AE60),
    Color(0xFF16A085),
    Color(0xFFB71C1C),
    Color(0xFF880E4F),
    Color(0xFF4A148C),
    Color(0xFF0D47A1),
    Color(0xFF00695C),
    Color(0xFF2E7D32),
    Color(0xFF9E9D24),
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

private fun priceModel(price: Int) = CarModel("", "", 0L, "", price, "", "", "", 0, 0, "", 0)
