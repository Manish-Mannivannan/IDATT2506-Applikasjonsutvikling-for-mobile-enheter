package ntnu.leksjon_06.tjener

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

class Server(
	private val port: Int = 12345,
	private val onStatus: (String) -> Unit,
	private val onMessage: (String) -> Unit,
	private val onClientCountChanged: (Int) -> Unit
) {

	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

	private var serverSocket: ServerSocket? = null
	private val clients = CopyOnWriteArrayList<ClientHandler>()
	private val clientIdSeq = AtomicInteger(1)

	@Volatile
	private var running = false

	fun start() {
		if (running) return
		running = true

		scope.launch {
			try {
				serverSocket = ServerSocket(port)
				val hostIp = try {
					// Display a best-effort IP for local Wi-Fi (may vary by device)
					InetAddress.getLocalHost().hostAddress ?: "0.0.0.0"
				} catch (_: Throwable) { "0.0.0.0" }

				onStatus("Lytter på $hostIp:$port\nVenter på klienter...")

				while (running) {
					val socket = try {
						serverSocket?.accept() ?: break
					} catch (_: Throwable) {
						break
					}
					if (!running) {
						socket.close()
						break
					}
					val id = clientIdSeq.getAndIncrement()
					val name = "Client-$id"
					val handler = ClientHandler(
						name = name,
						socket = socket,
						onMessage = { from, text ->
							// Broadcast to everyone with sender tag
							val line = "$from: $text"
							broadcast(line)
							onMessage(line)
						},
						onClosed = { h ->
							clients.remove(h)
							onStatus("Koblet fra: ${h.name} (tilkoblet: ${clients.size})")
							onClientCountChanged(clients.size)
						}
					)
					clients.add(handler)
					onStatus("Tilkoblet: $name fra ${socket.inetAddress.hostAddress} (tilkoblet: ${clients.size})")
					onClientCountChanged(clients.size)
					handler.start(scope)
				}
			} catch (e: Exception) {
				onStatus("Server-feil: ${e.message}")
			} finally {
				stop()
			}
		}
	}

	fun stop() {
		if (!running) return
		running = false
		try {
			serverSocket?.close()
		} catch (_: Throwable) {}
		serverSocket = null

		// Close all clients
		clients.forEach { it.close() }
		clients.clear()

		scope.cancel()
	}

	fun broadcast(line: String) {
		// Send to all connected clients
		val dead = mutableListOf<ClientHandler>()
		for (c in clients) {
			val ok = c.send(line)
			if (!ok) dead.add(c)
		}
		// Clean up any dead clients
		for (d in dead) {
			d.close()
			clients.remove(d)
		}
	}
}

private class ClientHandler(
	val name: String,
	private val socket: Socket,
	private val onMessage: (from: String, text: String) -> Unit,
	private val onClosed: (ClientHandler) -> Unit
) {
	private var reader: BufferedReader? = null
	private var writer: PrintWriter? = null
	private var job: Job? = null

	fun start(scope: CoroutineScope) {
		job = scope.launch(Dispatchers.IO) {
			try {
				reader = BufferedReader(InputStreamReader(socket.getInputStream()))
				writer = PrintWriter(socket.getOutputStream(), true)

				// Optional hello
				send("Velkommen! Du er $name")

				// Read loop
				while (isActive && !socket.isClosed) {
					val line = reader?.readLine() ?: break
					if (line.isBlank()) continue
					onMessage(name, line)
				}
			} catch (_: Throwable) {
				// ignore; handled in finally/close
			} finally {
				close()
				onClosed(this@ClientHandler)
			}
		}
	}

	fun send(text: String): Boolean {
		return try {
			writer?.println(text)
			true
		} catch (_: Throwable) {
			false
		}
	}

	fun close() {
		try { reader?.close() } catch (_: Throwable) {}
		try { writer?.close() } catch (_: Throwable) {}
		try { socket.close() } catch (_: Throwable) {}
		try { job?.cancel() } catch (_: Throwable) {}
	}
}
