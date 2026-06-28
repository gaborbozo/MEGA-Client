package hu.bozgab.megaclient.api

import hu.bozgab.megaclient.model.request.AuthNResponse
import hu.bozgab.megaclient.model.request.LoginRequest
import hu.bozgab.megaclient.util.HttpUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthenticationApi(private val client: HttpClient) {

    companion object {
        private const val subPath = "/api/authentication"
        private val url = HttpUtil.getBaseUrl(subPath)
    }

    suspend fun login(request: LoginRequest): AuthNResponse =
        client.post("${url}/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}