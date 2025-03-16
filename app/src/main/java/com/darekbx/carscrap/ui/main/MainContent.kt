package com.darekbx.carscrap.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darekbx.carscrap.navigation.AppNavHost
import com.darekbx.carscrap.ui.synchronization.SynchronizeBox
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainContent(
    innerPadding: PaddingValues,
    navController: NavHostController,
    mainViewModel: MainViewModel = koinViewModel()
) {
    val count by mainViewModel.count
    val firebaseSynchronization = false

    LaunchedEffect(Unit) {
        mainViewModel.fetchCount()
    }

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
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            AppNavHost(
                modifier = Modifier,
                navController = navController
            )
        }

        if (firebaseSynchronization) {
            SynchronizeBox(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }

        MenuRow(
            modifier = Modifier
                .padding(start = 8.dp, top = 4.dp, bottom = 8.dp, end = 8.dp),
            rowCount = count,
            navController = navController
        )
    }
}
