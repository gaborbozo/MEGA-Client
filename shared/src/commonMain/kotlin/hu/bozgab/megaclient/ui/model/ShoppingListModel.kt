package hu.bozgab.megaclient.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.bozgab.megaclient.model.ShoppingItem
import hu.bozgab.megaclient.repository.ShoppingListRepository
import hu.bozgab.megaclient.service.NotificationService
import hu.bozgab.megaclient.util.YearWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListModel(
    private val repository: ShoppingListRepository,
    private val notificationService: NotificationService
) {

    private val scope = CoroutineScope(Dispatchers.Main)

    var isLoading by mutableStateOf(false)
        private set

    // Main
    var items = mutableStateListOf<ShoppingItem>()
        private set
    var yearWeek by mutableStateOf(YearWeek.current())

    // State for creating
    var newProduct by mutableStateOf("")
    var isRepeatable by mutableStateOf(false)
    var isCreatingNew by mutableStateOf(false)

    var itemToDeleteId by mutableStateOf<Long?>(null)
    var itemToDropRepeatId by mutableStateOf<Long?>(null)

    init {
        loadItems()
    }

    fun loadItems() {
        isLoading = true
        scope.launch {
            repository.getByYearAndWeek(yearWeek.year, yearWeek.week).onSuccess {
                items.clear()
                items.addAll(it)
                isLoading = false
            }.onFailure {
                notificationService.showError(it.message ?: "Nem sikerült betölteni a bevásárlólistát")
                isLoading = false
            }
        }
    }

    fun nextWeek() {
        yearWeek = yearWeek.next()
        loadItems()
    }

    fun previousWeek() {
        yearWeek = yearWeek.previous()
        loadItems()
    }

    fun jumpToCurrentWeek() {
        yearWeek = YearWeek.current()
        loadItems()
    }

    fun create() {
        isCreatingNew = true
        newProduct = ""
        isRepeatable = false
    }

    fun cancelCreate() {
        isCreatingNew = false
        newProduct = ""
        isRepeatable = false
    }

    fun save() {
        if (newProduct.isBlank()) return
        isLoading = true
        scope.launch {
            repository.create(newProduct, isRepeatable).onSuccess {
                items.add(it)
                cancelCreate()
                isLoading = false
                notificationService.showSuccess("Tétel hozzáadva")
            }.onFailure {
                notificationService.showError(it.message ?: "Nem sikerült hozzáadni a tételt")
                isLoading = false
            }
        }
    }

    fun confirmDelete(id: Long) {
        itemToDeleteId = id
    }

    fun cancelDelete() {
        itemToDeleteId = null
    }

    fun delete() {
        val id = itemToDeleteId ?: return
        isLoading = true
        scope.launch {
            repository.delete(id).onSuccess {
                items.removeAll { it.id == id }
                itemToDeleteId = null
                isLoading = false
                notificationService.showSuccess("Tétel törölve")
            }.onFailure {
                notificationService.showError(it.message ?: "Nem sikerült törölni a tételt")
                isLoading = false
            }
        }
    }

    fun confirmDropRepeat(id: Long) {
        itemToDropRepeatId = id
    }

    fun cancelDropRepeat() {
        itemToDropRepeatId = null
    }

    fun dropRepeat() {
        val id = itemToDropRepeatId ?: return
        isLoading = true
        scope.launch {
            repository.dropRepeat(id).onSuccess {
                val index = items.indexOfFirst { item -> item.id == id }
                if (index != -1) {
                    items[index] = items[index].copy(repeatable = false)
                }
                itemToDropRepeatId = null
                isLoading = false
                notificationService.showSuccess("Ismétlődés eltávolítva")
            }.onFailure {
                notificationService.showError(it.message ?: "Nem sikerült eltávolítani az ismétlődést")
                isLoading = false
            }
        }
    }
}
