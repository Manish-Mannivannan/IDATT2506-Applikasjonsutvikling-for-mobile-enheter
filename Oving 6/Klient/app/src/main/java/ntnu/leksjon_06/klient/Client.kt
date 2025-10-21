package ntnu.leksjon_06.klient

import android.widget.TextView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Client(
	private val textView: TextView,
	private val SERVER_IP: String = "10.0.2.2",
	private val SERVER_PORT: Int = 12345,
) {

	/**
	 * Egendefinert set() som gjør at vi enkelt kan endre teksten som vises i skjermen til
	 * emulatoren med
	 *
	 * ```
	 * ui = "noe"
	 * ```
	 */
	private var ui: String? = ""
		set(str) {
			MainScope().launch { textView.text = str }
			field = str
		}


	fun start() {
		CoroutineScope(Dispatchers.IO).launch {
			ui = "Kobler til tjener..."
			try {
				Socket(SERVER_IP, SERVER_PORT).use { socket: Socket ->
					ui = "Koblet til tjener:\n$socket"

					delay(5000)

					readFromServer(socket)

					delay(5000)

					sendToServer(socket, "Heisann Tjener! Hyggelig å hilse på deg")

				}
			} catch (e: IOException) {
				e.printStackTrace()
				ui = e.message
			}
		}
	}

	private fun readFromServer(socket: Socket) {
		val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
		val message = reader.readLine()
		ui = "Melding fra tjeneren:\n$message"
	}

	private fun sendToServer(socket: Socket, message: String) {
		val writer = PrintWriter(socket.getOutputStream(), true)
		writer.println(message)
		ui = "Sendte følgende til tjeneren: \n\"$message\""
	}
}
