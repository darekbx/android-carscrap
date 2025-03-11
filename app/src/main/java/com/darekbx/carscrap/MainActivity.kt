package com.darekbx.carscrap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.darekbx.carscrap.ui.main.MainContent
import com.darekbx.carscrap.ui.theme.CarScrapTheme
import org.koin.androidx.compose.KoinAndroidContext

/**
 * TODO
 * Screens:
 * - Welcome screen - show when there's not serach filter set
 * - Filter definition screen
 *   - !There can be more than one filter, max 3
 *   - Car brand
 *   - Car model
 *   - Genersation
 *   - Optional filters:
 *      - Production year from, to
 *      - Engine power from, to (optional)
 *      - Fuel type (optional)
 *      - Gearbox (optional)
 *      - Salvaged yes/no (optional)
 * - Settings screen
 *   - Fetch data interval
 *   - Data count
 *   - DB size
 *   - Clear data
 * - Chart screen
 *   - Show chart with data (by tear)
 *   - Year selection
 *   - Trend line
 *   - Show/hide lines
 *   - Filters
 *     - Horse power
 *     - Gearbox
 * - List screen
 *   - Show list of cars in columns
 *   - Add sorting
 *   - Add filtering
 *   - Add/remove columns
 * - Statistics screen
 *   - Chart year to power
 *   - Average prices to year and power
 *   - Count of cars per year and power
 *
 * Functionality:
 * - Refreshing data in background service
 * - Synchronized only new data by date?
 * - Local database for storing data and filter
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                CarScrapTheme {
                    val navController = rememberNavController()
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainContent(innerPadding, navController)
                    }
                }
            }
        }
    }
}
