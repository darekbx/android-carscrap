package com.darekbx.carscrap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.carscrap.navigation.AppDestinations.Companion.filterIdArg
import com.darekbx.carscrap.ui.charts.ChartsView
import com.darekbx.carscrap.ui.filter.FilterView
import com.darekbx.carscrap.ui.filter.FiltersView
import com.darekbx.carscrap.ui.list.ListView
import com.darekbx.carscrap.ui.statistics.StatisticsView

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onFilterSelected: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = FiltersDestination.route,
        modifier = modifier
    ) {
        composable(
            route = ChartDestination.routeWithArgs,
            arguments = ChartDestination.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments
                ?.getString(filterIdArg)
                ?.let { filterId -> ChartsView(filterId = filterId) }
        }

        composable(
            route = ListDestination.routeWithArgs,
            arguments = ListDestination.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments
                ?.getString(filterIdArg)
                ?.let { filterId -> ListView(filterId = filterId) }
        }

        composable(
            route = StatisticsDestination.routeWithArgs,
            arguments = StatisticsDestination.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments
                ?.getString(filterIdArg)
                ?.let { filterId -> StatisticsView(filterId = filterId) }
        }

        composable(route = FiltersDestination.route) {
            FiltersView(
                onFilterSelected = {
                    onFilterSelected(it)
                    navController.navigate("${ChartDestination.route}?${filterIdArg}=${it}")
                },
                onAddNewFilter = {
                    navController.navigate(AddFilterDestination.route)
                }
            )
        }

        composable(route = AddFilterDestination.route) {
            FilterView {
                navController.popBackStack()
            }
        }
    }
}
