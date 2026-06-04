package hu.bozgab.megaclient.repository

import hu.bozgab.megaclient.api.UserApi
import hu.bozgab.megaclient.model.request.LoginRequest
import hu.bozgab.megaclient.model.response.AuthNResponse

class UserRepository(private val userApi: UserApi) {

    suspend fun login(username: String, password: String): Result<AuthNResponse> =
        runCatching { userApi.login(LoginRequest(username, password)) }

}
