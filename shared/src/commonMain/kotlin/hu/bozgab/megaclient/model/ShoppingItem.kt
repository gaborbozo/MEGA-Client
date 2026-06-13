package hu.bozgab.megaclient.model

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingItem(
    val id: Long,
    val product: String,
    val createdBy: String,
    val createdAt: Instant,
)
