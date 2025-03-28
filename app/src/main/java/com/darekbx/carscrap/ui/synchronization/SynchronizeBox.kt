package com.darekbx.carscrap.ui.synchronization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.carscrap.ui.theme.CarScrapTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.darekbx.carscrap.R
import com.darekbx.carscrap.repository.remote.RemoteData
import org.koin.androidx.compose.koinViewModel

private enum class ProgressStatus {
    IDLE,
    IN_PROGRESS,
    DONE
}

@Composable
fun SynchronizeBox(
    modifier: Modifier,
    synchronizeViewModel: SynchronizeViewModel = koinViewModel()
) {
    val status by synchronizeViewModel.synchronizationStep
    val isInProgress by synchronizeViewModel.inProgress
    var lastFetchDateTime by remember { mutableStateOf<String?>(null) }
    var lastStatus by remember { mutableStateOf<RemoteData.SynchronizationStep?>(null) }

    if (status !is RemoteData.SynchronizationStep.Failed) {
        lastStatus = status
    }

    LaunchedEffect(Unit) {
        lastFetchDateTime = synchronizeViewModel.lastFetchDateTime()
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = isInProgress,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 250)) +
                    expandVertically(animationSpec = tween(500, delayMillis = 250)),
            exit = fadeOut(animationSpec = tween(250)) +
                    shrinkVertically(animationSpec = tween(250))
        ) {
            StatusRow(Modifier.fillMaxWidth(), lastStatus)
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier,
                shape = MaterialTheme.shapes.medium,
                onClick = { synchronizeViewModel.synchronize() }) {
                Text("Synchronize data", color = MaterialTheme.colorScheme.secondary)
            }
            Text(
                text = lastFetchDateTime?.let {
                    buildAnnotatedString {
                        append("Last sync time")
                        appendLine()
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(it)
                        }
                    }
                } ?: buildAnnotatedString { append("Last sync: never") },
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
        }
    }

    status?.let {
        if (it is RemoteData.SynchronizationStep.Failed) {
            AlertDialog(
                onDismissRequest = { synchronizeViewModel.reset() },
                title = {
                    Text(
                        "Synchronization failed",
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                text = { Text("Error: ${it.error}") },
                confirmButton = {
                    Button(onClick = { synchronizeViewModel.reset() }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun StatusRow(
    modifier: Modifier,
    step: RemoteData.SynchronizationStep?
) {
    Row(
        modifier = modifier
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val steps = listOf(
            RemoteData.SynchronizationStep.Authenticate to R.drawable.ic_login,
            RemoteData.SynchronizationStep.FetchData to R.drawable.ic_cloud_download,
            RemoteData.SynchronizationStep.SaveTimestamp to R.drawable.ic_save,
            RemoteData.SynchronizationStep.StoreData to R.drawable.ic_database,
            RemoteData.SynchronizationStep.Completed to R.drawable.ic_done
        )

        steps.forEachIndexed { index, (currentStep, iconResId) ->
            val status = when {
                step == null -> ProgressStatus.IDLE
                step == RemoteData.SynchronizationStep.Completed -> ProgressStatus.DONE
                step == currentStep -> ProgressStatus.IN_PROGRESS
                step > currentStep -> ProgressStatus.DONE
                else -> ProgressStatus.IDLE
            }
            ProgressItem(iconResId = iconResId, status = status)
            if (index < steps.size - 1) {
                ProgressConnector(step != null && step > currentStep)
            }
        }
    }
}

@Composable
fun ProgressConnector(isCompleted: Boolean = false) {
    Spacer(
        Modifier
            .size(24.dp, 8.dp)
            .background(
                if (isCompleted) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.05F)
            )
    )
}

@Composable
private fun ProgressItem(
    iconResId: Int,
    status: ProgressStatus = ProgressStatus.IDLE
) {
    Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
        when (status) {
            ProgressStatus.IDLE -> IdleItem(iconResId)
            ProgressStatus.IN_PROGRESS -> InProgressItem(iconResId)
            ProgressStatus.DONE -> DoneItem()
        }
    }
}

@Composable
private fun DoneItem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_done),
            modifier = Modifier.size(24.dp),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
private fun InProgressItem(iconResId: Int) {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .rotate(-90F),
        color = MaterialTheme.colorScheme.primary,
        trackColor = Color.Black.copy(alpha = 0.05F),
        strokeCap = StrokeCap.Square,
        strokeWidth = 6.dp
    )
    Icon(
        painter = painterResource(iconResId),
        modifier = Modifier.size(24.dp),
        contentDescription = null,
        tint = Color.Gray
    )
}

@Composable
private fun IdleItem(iconResId: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.05F)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconResId),
            modifier = Modifier.size(24.dp),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Preview
@Composable
fun ProgressItemIdlePreview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White)) {
            ProgressItem(
                iconResId = R.drawable.ic_cloud_download,
                status = ProgressStatus.IDLE
            )
        }
    }
}

@Preview
@Composable
fun ProgressItemInProgressPreview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White)) {
            ProgressItem(
                iconResId = R.drawable.ic_cloud_download,
                status = ProgressStatus.IN_PROGRESS
            )
        }
    }
}

@Preview
@Composable
fun ProgressItemDonePreview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White)) {
            ProgressItem(
                iconResId = R.drawable.ic_cloud_download,
                status = ProgressStatus.DONE
            )
        }
    }
}

@Preview
@Composable
fun ProgressConnectorInProgressPreview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White)) {
            ProgressConnector(isCompleted = true)
        }
    }
}

@Preview
@Composable
fun ProgressConnectorDonePreview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White)) {
            ProgressConnector(isCompleted = false)
        }
    }
}

@Preview
@Composable
fun StatusRowS0Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), null)
        }
    }
}

@Preview
@Composable
fun StatusRowS1Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.Authenticate)
        }
    }
}

@Preview
@Composable
fun StatusRowS2Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.FetchData)
        }
    }
}

@Preview
@Composable
fun StatusRowS3Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.SaveTimestamp)
        }
    }
}

@Preview
@Composable
fun StatusRowS4Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.StoreData)
        }
    }
}

@Preview
@Composable
fun StatusRowS5Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.Completed)
        }
    }
}
