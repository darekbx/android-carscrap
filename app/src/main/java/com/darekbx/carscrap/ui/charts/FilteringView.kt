package com.darekbx.carscrap.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilteringView() {
    Box(
        Modifier
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.TopCenter
    ) {

    }
}