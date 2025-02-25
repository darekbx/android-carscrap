package com.darekbx.carscrap.ui.synchronization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.carscrap.di.appModule
import com.darekbx.carscrap.ui.theme.CarScrapTheme
import org.koin.compose.KoinApplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import com.darekbx.carscrap.R
import com.darekbx.carscrap.repository.remote.RemoteData
import org.koin.androidx.compose.koinViewModel

private enum class ProgressStatus {
    IDLE,
    IN_PROGRESS,
    DONE
}

@Composable
fun SynchronizeDialog(synchronizeViewModel: SynchronizeViewModel = koinViewModel()) {
    val status by synchronizeViewModel.synchronizationStep
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
    ) {
        StatusRow(Modifier.fillMaxWidth(), status)
        Button(onClick = {
            synchronizeViewModel.synchronize()
        }) { Text("Start") }
    }
}

@Composable
private fun StatusRow(modifier: Modifier, step: RemoteData.SynchronizationStep?) {
    Row(
        modifier = modifier
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val steps = listOf(
            RemoteData.SynchronizationStep.AUTHENTICATE to R.drawable.ic_login,
            RemoteData.SynchronizationStep.FETCH_DATA to R.drawable.ic_cloud_download,
            RemoteData.SynchronizationStep.SAVE_TIMESTAMP to R.drawable.ic_save,
            RemoteData.SynchronizationStep.STORE_DATA to R.drawable.ic_database,
            RemoteData.SynchronizationStep.COMPLETED to R.drawable.ic_done
        )

        steps.forEachIndexed { index, (currentStep, iconResId) ->
            val status = when {
                step == null -> ProgressStatus.IDLE
                step == RemoteData.SynchronizationStep.COMPLETED -> ProgressStatus.DONE
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
        if (status == ProgressStatus.IDLE) {
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
        } else if (status == ProgressStatus.IN_PROGRESS) {
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
        } else if (status == ProgressStatus.DONE) {
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
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.AUTHENTICATE)
        }
    }
}

@Preview
@Composable
fun StatusRowS2Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.FETCH_DATA)
        }
    }
}

@Preview
@Composable
fun StatusRowS3Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.SAVE_TIMESTAMP)
        }
    }
}

@Preview
@Composable
fun StatusRowS4Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.STORE_DATA)
        }
    }
}

@Preview
@Composable
fun StatusRowS5Preview() {
    CarScrapTheme {
        Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
            StatusRow(Modifier.fillMaxWidth(), RemoteData.SynchronizationStep.COMPLETED)
        }
    }
}

@Preview(device = Devices.PIXEL_6A)
@Composable
fun SynchronizeDialogPreview() {
    CarScrapTheme {
        KoinApplication(application = { modules(appModule) }) {
            Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Box(contentAlignment = Alignment.Center) {
                    SynchronizeDialog()
                }
            }
        }
    }
}
