package hu.bozgab.megaclient.model.request

import hu.bozgab.megaclient.util.AppColors
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class AuthNResponse(
    val userId: Long,
    val token: String,
    val expiration: Long,
    val theme: String? = AppColors.DEFAULT_COLOR_NAME
)