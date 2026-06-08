package hu.bozgab.megaclient.repository

import hu.bozgab.megaclient.api.NoteApi
import hu.bozgab.megaclient.model.Note
import hu.bozgab.megaclient.model.request.CreateNoteRequest
import hu.bozgab.megaclient.model.request.UpdateNoteRequest

class NoteRepository(private val noteApi: NoteApi) {

    suspend fun getAllNotes(): Result<List<Note>> =
        runCatching { noteApi.getAllNotes() }

    suspend fun createNote(note: String, color: String?): Result<Note> =
        runCatching { noteApi.createNote(CreateNoteRequest(note, color)) }

    suspend fun updateNote(id: Long, note: String?, color: String?): Result<Note> =
        runCatching { noteApi.updateNote(id, UpdateNoteRequest(note, color)) }

    suspend fun deleteNote(id: Long): Result<Unit> =
        runCatching { noteApi.deleteNote(id) }
}
