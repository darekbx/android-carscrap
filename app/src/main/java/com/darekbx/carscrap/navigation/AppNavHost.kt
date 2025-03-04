package com.darekbx.carscrap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.carscrap.ui.charts.ChartsView
import com.darekbx.carscrap.ui.list.ListView
import com.darekbx.carscrap.ui.statistics.StatisticsView

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ChartDestination.route,
        modifier = modifier
    ) {
        composable(route = ChartDestination.route) {
            ChartsView()
        }

        composable(route = ListDestination.route) {
            ListView()
        }

        composable(route = StatisticsDestination.route) {
            StatisticsView()
        }
    }
}
