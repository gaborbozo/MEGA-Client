package hu.bozgab.megaclient.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.bozgab.megaclient.model.ShoppingItem
import hu.bozgab.megaclient.repository.ShoppingListRepository
import hu.bozgab.megaclient.util.YearWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListModel(
    private val repository: ShoppingListRepository
) {

    private val scope = CoroutineScope(Dispatchers.Main)

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // Main
    var items = mutableStateListOf<ShoppingItem>()
        private set
    var yearWeek by mutableStateOf(YearWeek.current())

    // State for creating
    var newProduct by mutableStateOf("")
    var isCreatingNew by mutableStateOf(false)

    var itemToDeleteId by mutableStateOf<Long?>(null)

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
                error = it.message
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
    }

    fun cancelCreate() {
        isCreatingNew = false
        newProduct = ""
    }

    fun save() {
        if (newProduct.isBlank()) return
        isLoading = true
        scope.launch {
            repository.create(newProduct).onSuccess {
                items.add(it)
                cancelCreate()
                isLoading = false
            }.onFailure {
                error = it.message
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
            }.onFailure {
                error = it.message
                isLoading = false
            }
        }
    }
}
