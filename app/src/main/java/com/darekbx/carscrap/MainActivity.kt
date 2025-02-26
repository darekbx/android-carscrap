package com.darekbx.carscrap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.darekbx.carscrap.ui.synchronization.SynchronizeBox
import com.darekbx.carscrap.ui.theme.CarScrapTheme

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

                    /*var status by remember { mutableStateOf("Idle") }
                    val rd: RemoteData = koinInject()

                    LaunchedEffect(Unit) {
                        rd.synchronize {
                            status = this.name
                        }
                    }

                    Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {

                        Text(
                            text = "tatus $status"
                        )
                    }*/
                    Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                        SynchronizeBox()
                    }
                }
            }
        }
    }
}
