package hu.bozgab.megaclient.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hu.bozgab.megaclient.service.NotificationService
import hu.bozgab.megaclient.util.AppColors
import hu.bozgab.megaclient.util.Notification
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun NotificationOverlay(notificationService: NotificationService = koinInject()) {
    var currentNotification by remember { mutableStateOf<Notification?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    val fadeInDuration = 150
    val fadeOutDuration = 350

    LaunchedEffect(notificationService.notifications) {
        notificationService.notifications.collect { notification ->
            currentNotification = notification
            delay(fadeInDuration.toLong())
            isVisible = true
            delay(notification.durationMillis)
            isVisible = false
            delay(fadeOutDuration.toLong())
            currentNotification = null
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically(
                tween(
                    durationMillis = fadeInDuration,
                    easing = LinearOutSlowInEasing
                )
            ),
            exit = fadeOut() + slideOutVertically(
                animationSpec = tween(
                    durationMillis = fadeOutDuration,
                    easing = FastOutLinearInEasing
                )
            )
        ) {
            currentNotification?.let { notification ->
                val backgroundColor =
                    AppColors.notificationColors[notification.type.name] ?: Color.Gray

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .padding(16.dp)
                ) {
                    Text(
                        text = notification.message,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
