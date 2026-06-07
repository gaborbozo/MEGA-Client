package hu.bozgab.megaclient.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.bozgab.megaclient.model.NoteDTO
import hu.bozgab.megaclient.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteModel(private val noteRepository: NoteRepository) {

    private val scope = CoroutineScope(Dispatchers.Main)

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // Main
    var notes = mutableStateListOf<NoteDTO>()
        private set

    // State for editing
    var editingNoteId by mutableStateOf<Long?>(null)
    var editingText by mutableStateOf("")
    var isCreatingNew by mutableStateOf(false)

    // Unsaved changes dialog
    var showUnsavedDialog by mutableStateOf(false)
    private var pendingNavigation: (() -> Unit)? = null

    val hasUnsavedChanges: Boolean
        get() {
            if (editingNoteId == null) return false
            if (isCreatingNew) return editingText.isNotBlank()
            val originalNote = notes.find { it.id == editingNoteId }
            return originalNote?.note != editingText
        }

    init {
        loadNotes()
    }

    fun loadNotes() {
        isLoading = true
        scope.launch {
            noteRepository.getAllNotes().onSuccess {
                notes.clear()
                notes.addAll(it)
                isLoading = false
            }.onFailure {
                error = it.message
                isLoading = false
            }
        }
    }

    fun create() {
        val doCreate = {
            editingNoteId = -1L
            editingText = ""
            isCreatingNew = true
        }

        if (hasUnsavedChanges) {
            pendingNavigation = { doCreate() }
            showUnsavedDialog = true
        } else {
            doCreate()
        }
    }

    fun edit(note: NoteDTO) {
        val doEdit = {
            editingNoteId = note.id
            editingText = note.note
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
        isCreatingNew = false
    }

    fun save() {
        val id = editingNoteId ?: return
        isLoading = true
        scope.launch {
            if (isCreatingNew) {
                noteRepository.createNote(editingText).onSuccess {
                    notes.add(it)
                    cancelEdit()
                    isLoading = false
                }.onFailure {
                    error = it.message
                    isLoading = false
                }
            } else {
                noteRepository.updateNote(id, editingText).onSuccess { updated ->
                    val index = notes.indexOfFirst { it.id == id }
                    if (index != -1) {
                        notes[index] = updated
                    }
                    cancelEdit()
                    isLoading = false
                }.onFailure {
                    error = it.message
                    isLoading = false
                }
            }
        }
    }

    fun delete(id: Long) {
        isLoading = true
        scope.launch {
            noteRepository.deleteNote(id).onSuccess {
                notes.removeAll { it.id == id }
                isLoading = false
            }.onFailure {
                error = it.message
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
