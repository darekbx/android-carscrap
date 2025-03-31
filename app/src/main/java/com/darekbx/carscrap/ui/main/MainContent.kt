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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var selectedFilterId by remember { mutableStateOf<String?>(null) }
    val count by mainViewModel.count
    val firebaseSynchronization = false

    LaunchedEffect(selectedFilterId) {
        selectedFilterId?.let {
            mainViewModel.fetchCount(it)
        }
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
            ) { filterId ->
                selectedFilterId = filterId
            }
        }

        if (firebaseSynchronization) {
            SynchronizeBox(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }

        selectedFilterId?.let {
            MenuRow(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp, end = 8.dp),
                rowCount = count,
                selectedFilterId = it,
                navController = navController
            )
        }
    }
}
