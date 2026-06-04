package hu.bozgab.megaclient.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthNResponse(
    val userId: Long,
    val token: String,
    val expiration: Long
)
