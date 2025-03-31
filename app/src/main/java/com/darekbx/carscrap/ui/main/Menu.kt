package com.darekbx.carscrap.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.darekbx.carscrap.R
import com.darekbx.carscrap.navigation.AppDestinations.Companion.filterIdArg
import com.darekbx.carscrap.navigation.ChartDestination
import com.darekbx.carscrap.navigation.FiltersDestination
import com.darekbx.carscrap.navigation.ListDestination
import com.darekbx.carscrap.navigation.StatisticsDestination
import com.darekbx.carscrap.ui.theme.CarScrapTheme

enum class MenuItemType(val iconResId: Int, val route: String) {
    CHART(R.drawable.ic_chart, ChartDestination.route),
    LIST(R.drawable.ic_list, ListDestination.route),
    STATISTICS(R.drawable.ic_statistics, StatisticsDestination.route),
    FILTER(R.drawable.ic_filter, FiltersDestination.route),
}

@Composable
fun MenuRow(
    modifier: Modifier,
    rowCount: Int,
    selectedFilterId: String,
    navController: NavController
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    Row(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuItemType.entries.forEach { item ->
            MenuItem(
                item.iconResId,
                isActive = currentDestination?.route?.startsWith(item.route) ?: false,
            ) {
                navController.navigate(
                    "${item.route}?${filterIdArg}=${selectedFilterId}"
                )
            }
        }
        Spacer(Modifier.weight(1F))
        RowsCount(rowCount)
    }
}

@Composable
private fun MenuItem(iconResId: Int, isActive: Boolean = false, onClick: () -> Unit) {
    val borderModifier = Modifier.border(
        width = 2.dp,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary
    )
    Box(
        modifier = Modifier
            .aspectRatio(1F)
            .fillMaxHeight()
            .clickable { onClick() }
            .then(if (isActive) borderModifier else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconResId),
            modifier = Modifier.size(24.dp),
            contentDescription = null,
            tint = if (isActive) Color.Gray else MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun RowsCount(count: Int) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.W300)) {
                append("Data count: ")
            }
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(count.toString())
            }
        },
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(8.dp)
    )
}

@Preview
@Composable
fun MenuRowPreview() {
    CarScrapTheme {
        val context = LocalContext.current
        MenuRow(Modifier, 4231, "filter_id", NavController(context))
    }
}
