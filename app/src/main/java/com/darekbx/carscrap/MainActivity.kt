package com.darekbx.carscrap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.darekbx.carscrap.repository.remote.RemoteData
import com.darekbx.carscrap.ui.theme.CarScrapTheme
import org.koin.compose.koinInject

/**
 * TODO
 * + Add Koin
 * 2. Add Room database
 * 3. Add firebase auth + data fetcher
 * 4. Fetch data periodically + notification
 * 5. Display data list
 * 6. Display charts
 * 7. Display statistics
 * 8. Ability to set own filter
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarScrapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val rd: RemoteData = koinInject()

                    LaunchedEffect(Unit) {
                        rd.synchronize()
                    }

                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CarScrapTheme {
        Greeting("Android")
    }
}