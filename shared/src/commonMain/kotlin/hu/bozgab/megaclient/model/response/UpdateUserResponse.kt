package hu.bozgab.megaclient.model.response

import hu.bozgab.megaclient.util.AppColors
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserResponse(
    val theme: String? = AppColors.DEFAULT_COLOR_NAME
)
