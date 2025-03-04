package com.darekbx.carscrap.navigation


interface AppDestinations {
    val route: String
}

object ChartDestination : AppDestinations {
    override val route = "chart"
}

object ListDestination : AppDestinations {
    override val route = "list"
}
object StatisticsDestination : AppDestinations {
    override val route = "statistics"
}
