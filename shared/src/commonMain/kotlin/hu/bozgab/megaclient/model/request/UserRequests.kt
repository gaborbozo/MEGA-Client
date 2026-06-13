package hu.bozgab.megaclient.model.request

import hu.bozgab.megaclient.util.AppColors
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val theme: String? = null
)

@Serializable
data class UpdateUserResponse(
    val theme: String? = AppColors.DEFAULT_COLOR_NAME
)
