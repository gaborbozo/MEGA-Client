package hu.bozgab.megaclient.api

import hu.bozgab.megaclient.model.request.UpdateUserRequest
import hu.bozgab.megaclient.model.request.UpdateUserResponse
import hu.bozgab.megaclient.util.HttpUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserApi(private val client: HttpClient) {

    companion object {
        private const val subPath = "/api/user"
        private val url = "${HttpUtil.HOST}:${HttpUtil.PORT}${subPath}"
    }

    suspend fun update(request: UpdateUserRequest): UpdateUserResponse =
        client.patch(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
