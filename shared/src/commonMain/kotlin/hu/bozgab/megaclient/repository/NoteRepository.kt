package hu.bozgab.megaclient.repository

import hu.bozgab.megaclient.api.NoteApi
import hu.bozgab.megaclient.model.NoteDTO
import hu.bozgab.megaclient.model.request.CreateNoteRequest
import hu.bozgab.megaclient.model.request.UpdateNoteRequest

class NoteRepository(private val noteApi: NoteApi) {

    suspend fun getAllNotes(): Result<List<NoteDTO>> =
        runCatching { noteApi.getAllNotes() }

    suspend fun createNote(note: String): Result<NoteDTO> =
        runCatching { noteApi.createNote(CreateNoteRequest(note)) }

    suspend fun updateNote(id: Long, note: String): Result<NoteDTO> =
        runCatching { noteApi.updateNote(id, UpdateNoteRequest(note)) }

    suspend fun deleteNote(id: Long): Result<Unit> =
        runCatching { noteApi.deleteNote(id) }
}
