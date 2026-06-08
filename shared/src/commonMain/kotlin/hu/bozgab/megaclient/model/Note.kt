package hu.bozgab.megaclient.model

import hu.bozgab.megaclient.util.AppColors
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Note(
    val id: Long,
    val note: String,
    val createdBy: String,
    val createdAt: Instant,
    val color: String = AppColors.DEFAULT_COLOR_NAME,
    val updatedBy: String? = null,
    val updatedAt: Instant? = null
)
