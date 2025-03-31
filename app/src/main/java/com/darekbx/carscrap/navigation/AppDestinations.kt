package com.darekbx.carscrap.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.darekbx.carscrap.navigation.AppDestinations.Companion.filterIdArg

interface AppDestinations {
    val route: String

    companion object {
        const val filterIdArg = "filter_id"
    }
}

object ChartDestination : AppDestinations {
    override val route = "chart"
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
    val routeWithArgs = "${route}?$filterIdArg={${filterIdArg}}"
    val arguments = listOf(
        navArgument(filterIdArg) {
            nullable = false
            type = NavType.StringType
        }
    )
}

object StatisticsDestination : AppDestinations {
    override val route = "statistics"
    val routeWithArgs = "${route}?$filterIdArg={${filterIdArg}}"
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
