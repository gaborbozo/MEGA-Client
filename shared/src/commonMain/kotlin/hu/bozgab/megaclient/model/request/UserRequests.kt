package hu.bozgab.megaclient.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val theme: String? = null
)
