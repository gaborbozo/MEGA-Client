package hu.bozgab.megaclient.util

enum class NotificationType {
    INFO, ERROR, SUCCESS, WARNING
}

data class Notification(
    val message: String,
    val type: NotificationType,
    val durationMillis: Long
)
