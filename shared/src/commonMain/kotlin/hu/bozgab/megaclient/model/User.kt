package hu.bozgab.megaclient.model

import hu.bozgab.megaclient.util.AppColors

data class User(
    val id: Long,
    val name: String,
    val token: String,
    val expiration: Long,
    val theme: String = AppColors.DEFAULT_COLOR_NAME
)
