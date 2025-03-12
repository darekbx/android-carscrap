package com.darekbx.carscrap.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument


interface AppDestinations {
    val route: String
}

object ChartDestination : AppDestinations {
    override val route = "chart"
    const val filterIdArg = "filter_id"
    val routeWithArgs = "${route}?$filterIdArg={${filterIdArg}}"
    val arguments = listOf(
        navArgument(filterIdArg) {
            nullable = false
            type = NavType.StringType
        }
    )
}

object ListDestination : AppDestinations {
    override val route = "list"
    const val filterIdArg = "filter_id"
    val routeWithArgs = "${ListDestination.route}?$filterIdArg={${filterIdArg}}"
    val arguments = listOf(
        navArgument(filterIdArg) {
            nullable = false
            type = NavType.StringType
        }
    )
}

object StatisticsDestination : AppDestinations {
    override val route = "statistics"
    const val filterIdArg = "filter_id"
    val routeWithArgs = "${StatisticsDestination.route}?$filterIdArg={${filterIdArg}}"
    val arguments = listOf(
        navArgument(filterIdArg) {
            nullable = false
            type = NavType.StringType
        }
    )
}

object FiltersDestination : AppDestinations {
    override val route = "filters"
}

object AddFilterDestination : AppDestinations {
    override val route = "add_filter"
}
