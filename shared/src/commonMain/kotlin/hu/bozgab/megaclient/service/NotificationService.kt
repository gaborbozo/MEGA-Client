package hu.bozgab.megaclient.service

import hu.bozgab.megaclient.util.Notification
import hu.bozgab.megaclient.util.NotificationType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationService {
    private val _notifications = MutableSharedFlow<Notification>(extraBufferCapacity = 1)
    val notifications: SharedFlow<Notification> = _notifications.asSharedFlow()

    suspend fun showNotification(
        message: String,
        type: NotificationType = NotificationType.INFO,
        durationMillis: Long = 5000L
    ) {
        _notifications.emit(Notification(message, type, durationMillis))
    }

    suspend fun showError(message: String) {
        showNotification(message, NotificationType.ERROR, durationMillis = 10000L)
    }

    suspend fun showInfo(message: String) {
        showNotification(message, NotificationType.INFO, durationMillis = 5000L)
    }

    suspend fun showSuccess(message: String) {
        showNotification(message, NotificationType.SUCCESS, durationMillis = 3000L)
    }
}
