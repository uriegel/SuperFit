package de.uriegel.superfit.requests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

@Serializable
data class PingInput(val input: String)
@Serializable
data class PingOutput(val output: String)

suspend fun postPing(url: String, data: PingInput): Result<PingOutput> =
    post<PingInput, PingOutput>(url, data)

suspend inline fun <reified TIn, reified TOut> post(url: String, data: TIn): Result<TOut> =
    post(url, Json.encodeToString<TIn>(data))
        .map { Json.decodeFromString<TOut>(it) }

suspend fun post(urlString: String, data: String) =
    runCatching { tryPost(urlString, data) }

private suspend fun tryPost(urlString: String, data: String): String {
    return withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Accept-Encoding", "gzip")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doInput = true
        val writer = BufferedWriter(OutputStreamWriter(connection.outputStream))
        writer.write(data)
        writer.close()
        val result = connection.responseCode
        if (result != 200)
            throw HttpProtocolException(result, connection.responseMessage)
        val inStream =
            if (connection.contentEncoding == "gzip")
                GZIPInputStream(connection.inputStream)
            else
                connection.inputStream
        return@withContext readStream(inStream)
    }
}

private fun readStream(inString: InputStream): String {
    val response = StringBuffer()
    val reader = BufferedReader(InputStreamReader(inString))
    var line: String?
    while (reader.readLine().also { line = it } != null)
        response.append(line)
    reader.close()
    return response.toString()
}
