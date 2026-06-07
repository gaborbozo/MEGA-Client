package hu.bozgab.megaclient.api

import hu.bozgab.megaclient.model.NoteDTO
import hu.bozgab.megaclient.model.request.CreateNoteRequest
import hu.bozgab.megaclient.model.request.UpdateNoteRequest
import hu.bozgab.megaclient.util.HttpUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NoteApi(private val client: HttpClient) {

    companion object {
        private const val subPath = "/api/note"
        private val url = "${HttpUtil.HOST}:${HttpUtil.PORT}${subPath}"
    }

    suspend fun getAllNotes(): List<NoteDTO> =
        client.get(url).body()

    suspend fun createNote(request: CreateNoteRequest): NoteDTO =
        client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun updateNote(id: Long, request: UpdateNoteRequest): NoteDTO =
        client.patch("${url}/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun deleteNote(id: Long) {
        client.delete("${url}/$id")
    }
}
