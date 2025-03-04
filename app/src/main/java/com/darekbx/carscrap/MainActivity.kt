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

/**
 * TODO
 * 1. Fetch data periodically + notification
 * 2. Display data list
 * 3. Display charts
 * 4. Display statistics
 * 5. Ability to set own filter
 *
 * 6. Display chart: mileage with price for certain year
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarScrapTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(innerPadding, navController)
                }
            }
        }
    }
}
