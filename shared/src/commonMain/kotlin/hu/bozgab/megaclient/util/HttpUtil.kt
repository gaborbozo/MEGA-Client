package hu.bozgab.megaclient.util

import hu.bozgab.megaclient.BuildKonfig

object HttpUtil {
    val HOST = BuildKonfig.BACKEND_HOST
    val PORT = BuildKonfig.BACKEND_PORT.takeIf { it.isNotBlank() }

    fun getBaseUrl(subPath: String): String {
        return if (PORT != null) {
            "$HOST:$PORT$subPath"
        } else {
            "$HOST$subPath"
        }
    }
}
