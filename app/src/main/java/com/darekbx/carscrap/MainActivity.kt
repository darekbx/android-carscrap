package com.darekbx.carscrap

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
            }

            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationaleDialog()
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission")
            .setMessage("This app needs notification permission to show work results.")
            .setPositiveButton("OK") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
