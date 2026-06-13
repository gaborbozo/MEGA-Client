package hu.bozgab.megaclient.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateShoppingItemRequest(
    val product: String,
)