package ntnu.leksjon_06.klient

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

	private lateinit var statusText: TextView
	private lateinit var chatLog: TextView
	private lateinit var serverIpInput: EditText
	private lateinit var serverPortInput: EditText
	private lateinit var connectButton: Button
	private lateinit var messageInput: EditText
	private lateinit var sendButton: Button

	private var client: Client? = null
	private var connected = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		statusText = findViewById(R.id.statusText)
		chatLog = findViewById(R.id.chatLog)
		serverIpInput = findViewById(R.id.serverIpInput)
		serverPortInput = findViewById(R.id.serverPortInput)
		connectButton = findViewById(R.id.connectButton)
		messageInput = findViewById(R.id.messageInput)
		sendButton = findViewById(R.id.sendButton)

		// Default port
		serverPortInput.setText("12345")

		connectButton.setOnClickListener {
			if (!connected) {
				val ip = serverIpInput.text.toString().trim()
				val port = serverPortInput.text.toString().trim().toIntOrNull() ?: 12345
				if (ip.isEmpty()) {
					Toast.makeText(this, "Skriv inn server IP", Toast.LENGTH_SHORT).show()
					return@setOnClickListener
				}
				startClient(ip, port)
			} else {
				stopClient()
			}
		}

		sendButton.setOnClickListener {
			val msg = messageInput.text.toString().trim()
			if (msg.isNotEmpty()) {
				client?.send(msg)
				messageInput.setText("")
			}
		}
	}

	override fun onStop() {
		super.onStop()
		stopClient()
	}

	private fun startClient(ip: String, port: Int) {
		client = Client(
			serverIp = ip,
			serverPort = port,
			onStatus = { s -> runOnUiThread { statusText.text = s } },
			onConnected = {
				runOnUiThread {
					connected = true
					connectButton.text = "Disconnect"
					sendButton.isEnabled = true
				}
			},
			onDisconnected = {
				runOnUiThread {
					connected = false
					connectButton.text = "Connect"
					sendButton.isEnabled = false
				}
			},
			onMessage = { line -> appendChatLine(line) }
		)
		statusText.text = "Kobler til $ip:$port ..."
		client?.start()
	}

	private fun stopClient() {
		sendButton.isEnabled = false
		connected = false
		connectButton.text = "Connect"
		client?.stop()
		client = null
		statusText.text = "Status: Disconnected"
	}

	private fun appendChatLine(line: String) {
		runOnUiThread {
			if (chatLog.text.isEmpty()) {
				chatLog.text = line
			} else {
				chatLog.append("\n$line")
			}
			val scroll = chatLog.parent as? ScrollView
			scroll?.post { scroll.fullScroll(android.view.View.FOCUS_DOWN) }
		}
	}
}
