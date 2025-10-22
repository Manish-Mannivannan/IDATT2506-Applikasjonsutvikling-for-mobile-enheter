package ntnu.leksjon_06.tjener

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

	private lateinit var statusText: TextView
	private lateinit var chatLog: TextView
	private lateinit var messageInput: EditText
	private lateinit var sendButton: Button

	private var server: Server? = null
	private val port = 12345

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		statusText = findViewById(R.id.statusText)
		chatLog = findViewById(R.id.chatLog)
		messageInput = findViewById(R.id.messageInput)
		sendButton = findViewById(R.id.sendButton)

		sendButton.setOnClickListener {
			val text = messageInput.text.toString().trim()
			if (text.isNotEmpty()) {
				// Prefix so clients can see the origin
				server?.broadcast("Server: $text")
				appendChatLine("Server: $text")
				messageInput.setText("")
			}
		}
	}

	override fun onStart() {
		super.onStart()
		// Create server with UI callbacks
		server = Server(
			port = port,
			onStatus = { s -> runOnUiThread { statusText.text = s } },
			onMessage = { line -> appendChatLine(line) },
			onClientCountChanged = { count ->
				runOnUiThread { sendButton.isEnabled = (count > 0) }
			}
		)
		server?.start()
		statusText.text = "Starter Tjener på port $port ..."
	}

	override fun onStop() {
		super.onStop()
		sendButton.isEnabled = false
		server?.stop()
		server = null
		statusText.text = "Status: Stopped"
	}

	private fun appendChatLine(line: String) {
		runOnUiThread {
			// Append with newline
			if (chatLog.text.isEmpty()) {
				chatLog.text = line
			} else {
				chatLog.append("\n$line")
			}
			// Auto-scroll the ScrollView to bottom
			val scroll = chatLog.parent as? android.widget.ScrollView
			scroll?.post { scroll.fullScroll(android.view.View.FOCUS_DOWN) }
		}
	}
}
