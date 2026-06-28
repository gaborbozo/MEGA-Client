package hu.bozgab.megaclient.api

import hu.bozgab.megaclient.model.ShoppingItem
import hu.bozgab.megaclient.model.request.CreateShoppingItemRequest
import hu.bozgab.megaclient.util.HttpUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ShoppingListApi(private val client: HttpClient) {

    companion object {
        private const val subPath = "/api/shopping-list"
        private val url = HttpUtil.getBaseUrl(subPath)
    }

    suspend fun getByYearAndWeek(year: Int, week: Int): List<ShoppingItem> =
        client.get(url) {
            parameter("year", year)
            parameter("week", week)
        }.body()

    suspend fun create(request: CreateShoppingItemRequest): ShoppingItem =
        client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun delete(id: Long) {
        client.delete("${url}/$id")
    }
}
