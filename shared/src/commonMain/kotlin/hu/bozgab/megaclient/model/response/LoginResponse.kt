package hu.bozgab.megaclient.model.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val id: Long, val name: String)
