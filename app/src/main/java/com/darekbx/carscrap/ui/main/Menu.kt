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
import com.darekbx.carscrap.navigation.ChartDestination
import com.darekbx.carscrap.navigation.ListDestination
import com.darekbx.carscrap.navigation.StatisticsDestination
import com.darekbx.carscrap.ui.theme.CarScrapTheme

enum class MenuItemType(val iconResId: Int, val route: String) {
    CHART(R.drawable.ic_chart, ChartDestination.route),
    LIST(R.drawable.ic_list, ListDestination.route),
    STATISTICS(R.drawable.ic_statistics, StatisticsDestination.route)
}

@Composable
fun MenuRow(
    modifier: Modifier,
    rowCount: Int,
    navController: NavController
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    Row(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuItemType.entries.forEach { item ->
            MenuItem(
                item.iconResId,
                isActive = item.route == currentDestination?.route,
            ) {
                navController.navigate(item.route)
            }
        }
        Spacer(Modifier.weight(1F))
        RowsCount(rowCount)
    }
}

@Composable
private fun MenuItem(iconResId: Int, isActive: Boolean = false, onClick: () -> Unit) {
    val borderModifier = Modifier.border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
    Box(
        modifier = Modifier
            .aspectRatio(1F)
            .fillMaxHeight()
            .background(Color.Black.copy(alpha = 0.05F), CircleShape)
            .clickable { onClick() }
            .then(if (isActive) borderModifier else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconResId),
            modifier = Modifier.size(24.dp),
            contentDescription = null,
            tint = if (isActive) MaterialTheme.colorScheme.background else Color.Gray
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
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(8.dp)
    )
}

@Preview
@Composable
fun MenuRowPreview() {
    CarScrapTheme {
        val context = LocalContext.current
        MenuRow(Modifier, 4231, NavController(context))
    }
}
