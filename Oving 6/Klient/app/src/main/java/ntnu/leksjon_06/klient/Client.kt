package ntnu.leksjon_06.klient

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

class Client(
	private val serverIp: String,
	private val serverPort: Int,
	private val onStatus: (String) -> Unit,
	private val onConnected: () -> Unit,
	private val onDisconnected: () -> Unit,
	private val onMessage: (String) -> Unit
) {
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

	private var socket: Socket? = null
	private var reader: BufferedReader? = null
	private var writer: PrintWriter? = null
	@Volatile private var running = false

	fun start() {
		if (running) return
		running = true

		scope.launch {
			try {
				socket = Socket()
				// small timeout so UI doesn’t hang forever
				socket?.tcpNoDelay = true
				socket?.soTimeout = 0 // infinite read
				socket?.connect(InetSocketAddress(serverIp, serverPort), 5000)
				onStatus("Koblet til $serverIp:$serverPort")
				onConnected()

				reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
				writer = PrintWriter(socket!!.getOutputStream(), true)

				// hello to server (kan fjernes)
				send("Hei! (client connected)")

				// continuous read loop
				while (isActive && running && socket?.isClosed == false) {
					val line = reader?.readLine() ?: break
					if (line.isBlank()) continue
					onMessage(line)
				}
			} catch (e: Exception) {
				onStatus("Tilkoblingsfeil: ${e.message}")
			} finally {
				close()
				onDisconnected()
			}
		}
	}

	fun send(text: String) {
		// Always write from IO coroutine and flush explicitly didn't work outside the scope
		scope.launch(Dispatchers.IO) {
			try {
				writer?.println(text)
				writer?.flush()
			} catch (_: Throwable) { /* ignore; read loop will close on failure */ }
		}
	}

	fun stop() {
		running = false
		close()
		scope.cancel()
	}

	private fun close() {
		try { reader?.close() } catch (_: Throwable) {}
		try { writer?.close() } catch (_: Throwable) {}
		try { socket?.close() } catch (_: Throwable) {}
		reader = null
		writer = null
		socket = null
	}
}
