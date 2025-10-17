package ntnu.leksjon_05.http

import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

enum class HTTP { GET, POST }

private const val DEFAULT_ENCODING = "UTF-8"

/**
 * Lightweight HTTP helper around HttpURLConnection.
 * - Preserves cookies (CookieManager with ACCEPT_ALL)
 * - Proper POST (requestMethod=POST) with application/x-www-form-urlencoded; charset=UTF-8
 * - GET with query string
 * - UTF-8 by default, but respects server-provided charset
 * - Safe stream reading (no "null" line)
 * - Connect/read timeouts
 */
class HttpWrapper(private val baseUrl: String) {

	init {
		// Ensure one global cookie store so the server can track session/attempts
		if (CookieHandler.getDefault() == null) {
			CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
		}
	}

	private fun openConnection(url: String): HttpURLConnection {
		val conn = URL(url).openConnection() as HttpURLConnection
		conn.setRequestProperty("Accept-Charset", DEFAULT_ENCODING)
		conn.connectTimeout = 15_000
		conn.readTimeout = 15_000
		return conn
	}

	fun get(params: Map<String, String> = emptyMap()): String {
		val fullUrl = if (params.isNotEmpty()) {
			baseUrl + buildQuery(params)
		} else baseUrl

		val connection = openConnection(fullUrl)
		connection.requestMethod = "GET"

		return try {
			connection.inputStream.use { response ->
				readResponseBody(response, getCharSet(connection))
			}
		} finally {
			connection.disconnect()
		}
	}

	fun post(params: Map<String, String>): String {
		val connection = openConnection(baseUrl)
		connection.requestMethod = "POST"
		connection.doOutput = true
		val body = buildFormBody(params)

		val contentType = "application/x-www-form-urlencoded; charset=$DEFAULT_ENCODING"
		connection.setRequestProperty("Content-Type", contentType)

		return try {
			connection.outputStream.use { os ->
				os.write(body.toByteArray(UTF_8))
				os.flush()
			}
			connection.inputStream.use { response ->
				readResponseBody(response, getCharSet(connection))
			}
		} finally {
			connection.disconnect()
		}
	}

	/** Build query string for GET: ?k=v&k2=v2 (properly URL-encoded) */
	private fun buildQuery(params: Map<String, String>): String {
		if (params.isEmpty()) return ""
		val encoded = params.entries.joinToString("&") { (k, v) ->
			"${urlEncode(k)}=${urlEncode(v)}"
		}
		return "?$encoded"
	}

	/** Build x-www-form-urlencoded body for POST: k=v&k2=v2 (no leading '?') */
	private fun buildFormBody(params: Map<String, String>): String {
		return params.entries.joinToString("&") { (k, v) ->
			"${urlEncode(k)}=${urlEncode(v)}"
		}
	}

	private fun urlEncode(s: String): String = try {
		URLEncoder.encode(s, DEFAULT_ENCODING)
	} catch (e: UnsupportedEncodingException) {
		Log.e("HttpWrapper", "Encoding error: $e")
		URLEncoder.encode(s, "UTF-8")
	}

	/** Read entire response safely; never append "null" */
	private fun readResponseBody(inputStream: InputStream, charset: String?): String {
		return try {
			val cs = charset ?: DEFAULT_ENCODING
			BufferedReader(InputStreamReader(inputStream, cs)).use { br ->
				buildString {
					var line: String?
					while (true) {
						line = br.readLine()
						if (line == null) break
						append(line).append('\n')
					}
				}
			}
		} catch (e: Exception) {
			Log.e("HttpWrapper", "readResponseBody error: $e")
			"******* Problem reading from server *******\n$e"
		}
	}

	/** Read charset from Content-Type; default UTF-8 and null-safe */
	private fun getCharSet(connection: URLConnection): String? {
		val contentType = connection.contentType ?: return DEFAULT_ENCODING
		// e.g. "text/plain; charset=ISO-8859-1"
		val parts = contentType.split(";")
		for (p in parts) {
			val param = p.trim()
			if (param.startsWith("charset=", ignoreCase = true)) {
				val value = param.substringAfter("=").trim()
				Log.i("HttpWrapper", "Server charset: $value")
				return value
			}
		}
		Log.i("HttpWrapper", "Server charset not specified; using $DEFAULT_ENCODING")
		return DEFAULT_ENCODING
	}
}
