package com.darekbx.carscrap.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.darekbx.carscrap.ui.filter.FilterViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatisticsView(filterId: String = "", todo: FilterViewModel = koinViewModel()) {

    val inProgress by todo.inProgress

    Button(onClick = {
        todo.scrap(filterId)
    } ) {
        Text("Fetch filter ${filterId}")
    }

    if (inProgress) {
        ProgressBox()
    }
}

// TODO move to common
@Composable
private fun ProgressBox() {
    Popup {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
        ) {
            CircularProgressIndicator(
                Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        }
    }
}
