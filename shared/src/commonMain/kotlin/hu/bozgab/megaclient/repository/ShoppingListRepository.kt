package hu.bozgab.megaclient.repository

import hu.bozgab.megaclient.api.ShoppingListApi
import hu.bozgab.megaclient.model.ShoppingItem
import hu.bozgab.megaclient.model.request.CreateShoppingItemRequest

class ShoppingListRepository(private val api: ShoppingListApi) {

    suspend fun getByYearAndWeek(year: Int, week: Int): Result<List<ShoppingItem>> =
        runCatching { api.getByYearAndWeek(year, week) }

    suspend fun create(product: String): Result<ShoppingItem> =
        runCatching { api.create(CreateShoppingItemRequest(product)) }

    suspend fun delete(id: Long): Result<Unit> =
        runCatching { api.delete(id) }

}
