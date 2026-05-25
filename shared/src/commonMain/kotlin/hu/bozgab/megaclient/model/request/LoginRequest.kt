package hu.bozgab.megaclient.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)
