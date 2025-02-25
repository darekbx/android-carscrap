package com.darekbx.carscrap

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.darekbx.carscrap.repository.remote.RemoteData
import com.darekbx.carscrap.ui.synchronization.SynchronizeDialog
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
                    Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                        SynchronizeDialog()
                    }
                }
            }
        }
    }
}
