package hu.bozgab.megaclient.api

import hu.bozgab.megaclient.model.Note
import hu.bozgab.megaclient.model.request.CreateNoteRequest
import hu.bozgab.megaclient.model.request.UpdateNoteRequest
import hu.bozgab.megaclient.util.HttpUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NoteApi(private val client: HttpClient) {

    companion object {
        private const val subPath = "/api/note"
        private val url = HttpUtil.getBaseUrl(subPath)
    }

    suspend fun getAll(): List<Note> =
        client.get(url).body()

    suspend fun create(request: CreateNoteRequest): Note =
        client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun update(id: Long, request: UpdateNoteRequest): Note =
        client.patch("${url}/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun delete(id: Long) {
        client.delete("${url}/$id")
    }
}
