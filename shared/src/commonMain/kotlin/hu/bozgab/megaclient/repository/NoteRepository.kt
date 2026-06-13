package hu.bozgab.megaclient.repository

import hu.bozgab.megaclient.api.NoteApi
import hu.bozgab.megaclient.model.Note
import hu.bozgab.megaclient.model.request.CreateNoteRequest
import hu.bozgab.megaclient.model.request.UpdateNoteRequest

class NoteRepository(private val noteApi: NoteApi) {

    suspend fun getAll(): Result<List<Note>> =
        runCatching { noteApi.getAll() }

    suspend fun create(note: String, color: String?): Result<Note> =
        runCatching { noteApi.create(CreateNoteRequest(note, color)) }

    suspend fun update(id: Long, note: String?, color: String?): Result<Note> =
        runCatching { noteApi.update(id, UpdateNoteRequest(note, color)) }

    suspend fun delete(id: Long): Result<Unit> =
        runCatching { noteApi.delete(id) }

}
