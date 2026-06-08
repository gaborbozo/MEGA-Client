package hu.bozgab.megaclient.util

import androidx.compose.ui.graphics.Color

data class AppColor(
    val primary: Color,
    val secondary: Color
)

object AppColors {
    const val DEFAULT_COLOR_NAME = "grey"

    val colors = mapOf(
        "grey" to AppColor(Color(0xFFEEEEEE), Color(0xFFF5F5F5)),
        "red" to AppColor(Color(0xFFFFCDD2), Color(0xFFFFEBEE)),
        "green" to AppColor(Color(0xFFC8E6C9), Color(0xFFE8F5E9)),
        "blue" to AppColor(Color(0xFFBBDEFB), Color(0xFFE3F2FD)),
        "brown" to AppColor(Color(0xFFD7CCC8), Color(0xFFEFEBE9)),
        "orange" to AppColor(Color(0xFFFFE0B2), Color(0xFFFFF3E0)),
        "yellow" to AppColor(Color(0xFFFFF9C4), Color(0xFFFFFDE7)),
        "pink" to AppColor(Color(0xFFF8BBD0), Color(0xFFFCE4EC))
    )

    fun getColor(name: String?): AppColor =
        colors[name?.lowercase()] ?: colors[DEFAULT_COLOR_NAME]!!

    fun getPrimary(name: String?): Color = getColor(name).primary

    fun getSecondary(name: String?): Color = getColor(name).secondary
}
