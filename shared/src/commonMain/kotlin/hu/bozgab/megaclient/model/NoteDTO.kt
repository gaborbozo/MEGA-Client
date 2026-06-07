package hu.bozgab.megaclient.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class NoteDTO(
    val id: Long,
    val note: String,
    val createdBy: String,
    val createdAt: Instant
)
