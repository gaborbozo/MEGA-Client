package hu.bozgab.megaclient.repository

import hu.bozgab.megaclient.api.UserApi
import hu.bozgab.megaclient.model.response.LoginResponse

class UserRepository(private val userApi: UserApi) {

    suspend fun login(username: String, password: String): Result<LoginResponse> =
        Result.success(LoginResponse(1, "Gabor"))
//runCatching { userApi.login(LoginRequest(username, password)) }

}