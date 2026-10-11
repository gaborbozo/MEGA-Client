package hu.bozgab.megaclient.repository

import hu.bozgab.megaclient.api.ShoppingListApi
import hu.bozgab.megaclient.model.ShoppingItem
import hu.bozgab.megaclient.model.request.CreateShoppingItemRequest

class ShoppingListRepository(private val api: ShoppingListApi) {

    suspend fun getByYearAndWeek(year: Int, week: Int): Result<List<ShoppingItem>> =
        runCatching { api.getByYearAndWeek(year, week) }

    suspend fun create(product: String, repeatable: Boolean = false): Result<ShoppingItem> =
        runCatching { api.create(CreateShoppingItemRequest(product, repeatable)) }

    suspend fun delete(id: Long): Result<Unit> =
        runCatching { api.delete(id) }

    suspend fun dropRepeat(id: Long): Result<Unit> =
        runCatching { api.dropRepeat(id) }

}
