package hu.bozgab.megaclient.api

import hu.bozgab.megaclient.util.HttpUtil
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class GeriApi(private val client: HttpClient) {

    companion object {
        private const val subPath = "/api/geri"
        private val url = HttpUtil.getBaseUrl(subPath)
    }

    suspend fun upload(file: PlatformFile): Long {
        val bytes = file.readBytes()

        return client.post("${url}/upload") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("request", bytes, Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"${file.nameWithoutExtension}\""
                            )
                            append(HttpHeaders.ContentType, file.mimeType().toString())
                        })
                    }
                ))
        }.body()
    }

    suspend fun getRandom(): HttpResponse =
        client.get("${url}/random")

    suspend fun delete(id: Long): HttpResponse =
        client.delete("${url}/$id")
}
