package hu.bozgab.megaclient.repository

import hu.bozgab.megaclient.api.GeriApi
import io.github.vinceglb.filekit.PlatformFile
import io.ktor.client.statement.HttpResponse

class GeriRepository(private val api: GeriApi) {

    suspend fun upload(file: PlatformFile): Result<Long> =
        runCatching { api.upload(file) }

    suspend fun getRandom(): Result<HttpResponse> =
        runCatching { api.getRandom() }

    suspend fun delete(id: Long): Result<Unit> =
        runCatching { api.delete(id) }
}
