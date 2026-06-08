package hu.bozgab.megaclient.repository

import hu.bozgab.megaclient.api.AuthenticationApi
import hu.bozgab.megaclient.api.UserApi
import hu.bozgab.megaclient.model.request.LoginRequest
import hu.bozgab.megaclient.model.request.UpdateUserRequest
import hu.bozgab.megaclient.model.response.AuthNResponse
import hu.bozgab.megaclient.model.response.UpdateUserResponse

class UserRepository(
    private val authenticationApi: AuthenticationApi,
    private val userApi: UserApi
) {

    suspend fun login(username: String, password: String): Result<AuthNResponse> =
        runCatching { authenticationApi.login(LoginRequest(username, password)) }

    suspend fun updateUser(request: UpdateUserRequest): Result<UpdateUserResponse> =
        runCatching { userApi.updateUser(request) }

}
