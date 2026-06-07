package hu.bozgab.megaclient.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.bozgab.megaclient.service.UserStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsModel(private val userStorage: UserStorage) {

    private val scope = CoroutineScope(Dispatchers.Main)

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // Main
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    val canLogin: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading

    fun login() {
        isLoading = true
        error = null
        scope.launch {
            val result = userStorage.login(username, password)
            result.onSuccess {
                isLoading = false
            }.onFailure {
                error = it.message ?: "Login failed"
                isLoading = false
            }
        }
    }

    fun logout() {
        userStorage.logout()
    }
}
