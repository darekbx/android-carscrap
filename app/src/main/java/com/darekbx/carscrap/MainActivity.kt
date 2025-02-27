package com.darekbx.carscrap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.darekbx.carscrap.ui.MenuItemType
import com.darekbx.carscrap.ui.MenuRow
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

                    Column(
                        Modifier
                            .padding(innerPadding)
                            .padding(bottom = 4.dp)
                            .fillMaxSize()
                    ) {

                        Box(
                            Modifier
                                .padding(start = 8.dp, top = 8.dp, bottom = 4.dp, end = 8.dp)
                                .fillMaxSize()
                                .weight(1F)
                                .background(Color(0xFF282828), MaterialTheme.shapes.medium),
                            contentAlignment = Alignment.TopCenter
                        ) {

                        }

                        SynchronizeBox(Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        )

                        MenuRow(
                            modifier = Modifier
                                .padding(start = 8.dp, top = 4.dp, bottom = 8.dp, end = 8.dp),
                            rowCount = 2431,
                            activeMenuItem = MenuItemType.LIST
                        ) {
                            // TODO
                        }
                    }
                }
            }
        }
    }

}
