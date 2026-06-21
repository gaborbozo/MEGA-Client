package hu.bozgab.megaclient.util

import androidx.compose.ui.graphics.Color

data class AppColor(
    val primary: Color,
    val secondary: Color
)

object AppColors {
    const val DEFAULT_COLOR_NAME = "grey"

    val colors = mapOf(
        "grey" to AppColor(
            primary = Color(0xFF9E9E9E),
            secondary = Color(0xFFF5F5F5)
        ),

        "black" to AppColor(
            primary = Color(0xFF121212),
            secondary = Color(0xFF2C2C2C)
        ),

        "brown" to AppColor(
            primary = Color(0xFF6D4C41),
            secondary = Color(0xFFEFEBE9)
        ),

        "pink" to AppColor(
            primary = Color(0xFFC2185B),
            secondary = Color(0xFFFCE4EC)
        )
    )

    fun getColor(name: String?): AppColor =
        colors[name?.lowercase()] ?: colors[DEFAULT_COLOR_NAME]!!

    fun getPrimary(name: String?): Color = getColor(name).primary

    fun getSecondary(name: String?): Color = getColor(name).secondary

    val notificationColors = mapOf(
        "INFO" to Color(0xFF2196F3),
        "ERROR" to Color(0xFFF44336),
        "SUCCESS" to Color(0xFF4CAF50),
        "WARNING" to Color(0xFFFF9800)
    )
}
