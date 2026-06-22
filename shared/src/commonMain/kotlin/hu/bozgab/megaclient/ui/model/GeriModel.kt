package hu.bozgab.megaclient.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.bozgab.megaclient.repository.GeriRepository
import hu.bozgab.megaclient.service.NotificationService
import hu.bozgab.megaclient.util.getImage
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeriModel(
    private val repository: GeriRepository,
    private val notificationService: NotificationService
) {

    private val scope = CoroutineScope(Dispatchers.Main)

    var isLoading by mutableStateOf(false)
        private set

    // Main

    var currentImageBytes by mutableStateOf<ByteArray?>(null)
        private set

    var currentImageId by mutableStateOf<Long?>(null)
        private set

    init {
        loadRandomImage()
    }

    fun loadRandomImage() {
        scope.launch {
            isLoading = true
            val result = repository.getRandom()

            if (result.isSuccess) {
                val response = result.getOrThrow()
                try {
                    val bytes = response.bodyAsBytes()
                    currentImageId = response.headers["X-File-Id"]?.toLongOrNull()
                    if (bytes.isEmpty()) {
                        currentImageBytes = null
                        notificationService.showError("Üres kép érkezett")
                    } else {
                        currentImageBytes = bytes
                    }
                } catch (e: Exception) {
                    notificationService.showError("Hiba a kép feldolgozásakor: ${e.message}")
                }
            } else {
                val exception = result.exceptionOrNull()
                notificationService.showError("Hálózati hiba: ${exception?.message}")
            }
            isLoading = false
        }
    }

    fun uploadImage() {
        scope.launch {
            val image = getImage() ?: return@launch

            isLoading = true
            val result = repository.upload(image)

            if (result.isSuccess) {
                notificationService.showSuccess("Kép feltöltve")
                loadRandomImage()
            } else {
                val exception = result.exceptionOrNull()
                notificationService.showError(exception?.message ?: "Sikertelen feltöltés")
            }
            isLoading = false
        }
    }

    fun deleteCurrentImage() {
        val id = currentImageId ?: return
        scope.launch {
            isLoading = true
            val result = repository.delete(id)

            if (result.isSuccess) {
                notificationService.showSuccess("Kép törölve")
                currentImageBytes = null
                currentImageId = null
                loadRandomImage()
            } else {
                notificationService.showError("Nem sikerült törölni a képet")
            }
            isLoading = false
        }
    }

}
