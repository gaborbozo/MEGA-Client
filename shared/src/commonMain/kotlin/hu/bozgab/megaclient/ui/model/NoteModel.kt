package hu.bozgab.megaclient.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.bozgab.megaclient.model.Note
import hu.bozgab.megaclient.repository.NoteRepository
import hu.bozgab.megaclient.service.NotificationService
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.util.AppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteModel(
    private val repository: NoteRepository,
    private val userStorage: UserStorage,
    private val notificationService: NotificationService
) {

    private val scope = CoroutineScope(Dispatchers.Main)

    var isLoading by mutableStateOf(false)
        private set

    // Main
    var notes = mutableStateListOf<Note>()
        private set

    // State for editing
    var editingNoteId by mutableStateOf<Long?>(null)
    var editingText by mutableStateOf("")
    var editingColor by mutableStateOf(userStorage.user?.theme ?: AppColors.DEFAULT_COLOR_NAME)
    var isCreatingNew by mutableStateOf(false)

    // Unsaved changes dialog
    var showUnsavedDialog by mutableStateOf(false)
    private var pendingNavigation: (() -> Unit)? = null

    val hasUnsavedChanges: Boolean
        get() {
            if (editingNoteId == null) return false
            if (isCreatingNew) return editingText.isNotBlank() || editingColor != (userStorage.user?.theme
                ?: AppColors.DEFAULT_COLOR_NAME)
            val originalNote = notes.find { it.id == editingNoteId }
            return originalNote?.note != editingText || originalNote.color != editingColor
        }

    init {
        loadNotes()
    }

    fun loadNotes() {
        isLoading = true
        scope.launch {
            repository.getAll().onSuccess {
                notes.clear()
                notes.addAll(it.sortedByDescending { note -> note.updatedAt ?: note.createdAt })
                isLoading = false
            }.onFailure {
                notificationService.showError(it.message ?: "Nem sikerült betölteni a jegyzeteket")
                isLoading = false
            }
        }
    }

    fun create() {
        val doCreate = {
            editingNoteId = -1L
            editingText = ""
            editingColor = userStorage.user?.theme ?: AppColors.DEFAULT_COLOR_NAME
            isCreatingNew = true
        }

        if (hasUnsavedChanges) {
            pendingNavigation = { doCreate() }
            showUnsavedDialog = true
        } else {
            doCreate()
        }
    }

    fun edit(note: Note) {
        val doEdit = {
            editingNoteId = note.id
            editingText = note.note
            editingColor = note.color
            isCreatingNew = false
        }

        if (hasUnsavedChanges) {
            pendingNavigation = { doEdit() }
            showUnsavedDialog = true
        } else {
            doEdit()
        }
    }

    fun cancelEdit() {
        editingNoteId = null
        editingText = ""
        editingColor = userStorage.user?.theme ?: AppColors.DEFAULT_COLOR_NAME
        isCreatingNew = false
    }

    fun save() {
        val id = editingNoteId ?: return
        isLoading = true
        scope.launch {
            if (isCreatingNew) {
                repository.create(editingText, editingColor).onSuccess {
                    notes.add(0, it)
                    cancelEdit()
                    isLoading = false
                    notificationService.showSuccess("Jegyzet létrehozva")
                }.onFailure {
                    notificationService.showError(
                        it.message ?: "Nem sikerült létrehozni a jegyzetet"
                    )
                    isLoading = false
                }
            } else {
                repository.update(id, editingText, editingColor).onSuccess { updated ->
                    val index = notes.indexOfFirst { it.id == id }
                    if (index != -1) {
                        notes[index] = updated

                        val sortedList = notes.sortedByDescending { it.updatedAt }
                        notes.clear()
                        notes.addAll(sortedList)
                    }
                    cancelEdit()
                    isLoading = false
                    notificationService.showSuccess("Jegyzet frissítve")
                }.onFailure {
                    notificationService.showError(
                        it.message ?: "Nem sikerült frissíteni a jegyzetet"
                    )
                    isLoading = false
                }
            }
        }
    }

    fun delete(id: Long) {
        isLoading = true
        scope.launch {
            repository.delete(id).onSuccess {
                notes.removeAll { it.id == id }
                isLoading = false
                notificationService.showSuccess("Jegyzet törölve")
            }.onFailure {
                notificationService.showError(it.message ?: "Nem sikerült törölni a jegyzetet")
                isLoading = false
            }
        }
    }

    fun onDismissUnsavedDialog(confirm: Boolean) {
        if (confirm) {
            pendingNavigation?.invoke()
        }
        pendingNavigation = null
        showUnsavedDialog = false
    }

    fun tryExitEditMode(onExit: () -> Unit) {
        if (hasUnsavedChanges) {
            pendingNavigation = onExit
            showUnsavedDialog = true
        } else {
            onExit()
        }
    }
}
