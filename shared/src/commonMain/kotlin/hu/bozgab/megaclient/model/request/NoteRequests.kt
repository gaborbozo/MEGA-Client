package hu.bozgab.megaclient.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val note: String,
)

@Serializable
data class UpdateNoteRequest(
    val note: String?,
)
