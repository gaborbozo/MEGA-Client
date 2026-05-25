package hu.bozgab.megaclient.api

import hu.bozgab.megaclient.model.request.LoginRequest
import hu.bozgab.megaclient.model.response.LoginResponse
import hu.bozgab.megaclient.util.HttpUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserApi(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): LoginResponse =
        client.post("${HttpUtil.BASE_URL}/user") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
