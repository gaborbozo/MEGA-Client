package hu.bozgab.megaclient.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.bozgab.megaclient.model.User
import hu.bozgab.megaclient.model.request.UpdateUserRequest
import hu.bozgab.megaclient.repository.UserRepository
import hu.bozgab.megaclient.util.AppColors

class UserStorage(private val userRepository: UserRepository) {
    var user by mutableStateOf<User?>(null)
        private set

    suspend fun login(username: String, password: String): Result<Unit> =
        userRepository.login(username, password)
            .onSuccess { response ->
                user = User(
                    id = response.userId,
                    name = username,
                    token = response.token,
                    expiration = response.expiration,
                    theme = response.theme ?: AppColors.DEFAULT_COLOR_NAME
                )
            }
            .map { }

    suspend fun updateTheme(theme: String): Result<Unit> =
        userRepository.update(UpdateUserRequest(theme = theme))
            .onSuccess { response ->
                user = user?.copy(theme = response.theme ?: AppColors.DEFAULT_COLOR_NAME)
            }
            .map { }

    fun logout() {
        user = null
    }
}
