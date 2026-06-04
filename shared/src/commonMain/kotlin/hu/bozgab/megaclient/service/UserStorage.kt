package hu.bozgab.megaclient.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.bozgab.megaclient.model.User
import hu.bozgab.megaclient.repository.UserRepository

class UserStorage(private val userRepository: UserRepository) {
    var user by mutableStateOf<User?>(null)
        private set

    suspend fun login(username: String, password: String): Result<Unit> =
        userRepository.login(username, password)
            .onSuccess { response ->
                user = User(response.userId, username, response.token, response.expiration)
            }
            .map { }

    fun logout() {
        user = null
    }
}
