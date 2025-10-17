package ntnu.leksjon_05.http

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.view.View
import android.widget.ScrollView

private const val TALLSPILL_URL = "https://bigdata.idi.ntnu.no/mobil/tallspill.jsp"

class MainActivity : AppCompatActivity() {
	private lateinit var scrollRoot: ScrollView

	private lateinit var http: HttpWrapper

	private lateinit var inputName: EditText
	private lateinit var inputCard: EditText
	private lateinit var inputGuess: EditText
	private lateinit var btnStart: Button
	private lateinit var btnSend: Button
	private lateinit var textStatus: TextView
	private lateinit var textLog: TextView

	private var lastRegisteredName: String? = null
	private var lastRegisteredCard: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		http = HttpWrapper(TALLSPILL_URL)

		scrollRoot = findViewById(R.id.scrollRoot)

		inputName = findViewById(R.id.inputName)
		inputCard = findViewById(R.id.inputCard)
		inputGuess = findViewById(R.id.inputGuess)
		btnStart = findViewById(R.id.btnStart)
		btnSend = findViewById(R.id.btnSend)
		textStatus = findViewById(R.id.textStatus)
		textLog = findViewById(R.id.textLog)
		textLog.movementMethod = ScrollingMovementMethod()

		btnStart.setOnClickListener { startNewGame() }
		btnSend.setOnClickListener { sendGuess() }
	}

	private fun startNewGame() {
		val navn = inputName.text?.toString()?.trim().orEmpty()
		val kort = inputCard.text?.toString()?.trim().orEmpty()
		if (navn.isBlank() || kort.isBlank()) {
			setStatus("Fyll inn navn og kortnummer.")
			return
		}

		// Clear screen ONLY if identity changed OR previous round disabled guessing
		val identityChanged = (navn != lastRegisteredName) || (kort != lastRegisteredCard)
		clearForNewGame(force = identityChanged || !inputGuess.isEnabled)

		setUiEnabled(false)
		lifecycleScope.launch {
			val response = withContext(Dispatchers.IO) {
				try {
					http.post(mapOf("navn" to navn, "kortnummer" to kort))
				} catch (e: Exception) {
					Log.e("MainActivity", "Register error", e)
					"Nettverksfeil: ${e.message}"
				}
			}
			appendLog("POST /tallspill.jsp {navn=..., kortnummer=...}\n$response")
			setStatus(response.trim())

			val ready = response.contains("Oppgi et tall mellom", ignoreCase = true)
			setGuessEnabled(ready)

			//Record user data if server accepts
			if (ready) {
				lastRegisteredName = navn
				lastRegisteredCard = kort
			}
			setUiEnabled(true)
		}
	}

	private fun clearForNewGame(force: Boolean = false) {
		// Clear if identity changed OR New game (round over)
		if (force) {
			textStatus.text = ""
			textLog.text = ""
			inputGuess.setText("")
			setGuessEnabled(false)
			scrollRoot.post { scrollRoot.scrollTo(0, 0) }
			return
		}
	}

	private fun sendGuess() {
		val tall = inputGuess.text?.toString()?.trim().orEmpty()
		if (tall.isBlank()) {
			setStatus("Skriv inn et tall.")
			return
		}
		setUiEnabled(false)

		lifecycleScope.launch {
			val response = withContext(Dispatchers.IO) {
				try {
					http.post(mapOf("tall" to tall))
				} catch (e: Exception) {
					Log.e("MainActivity", "Guess error", e)
					"Nettverksfeil: ${e.message}"
				}
			}
			appendLog("POST /tallspill.jsp {tall=$tall}\n$response")
			val msg = response.trim()
			setStatus(msg)

			// Detect server states
			val won = msg.contains("du har vunnet", ignoreCase = true)

			val outOfTries = msg.contains("Beklager ingen flere sjanser", ignoreCase = true) ||
					msg.contains("registrer kortnummer og navn", ignoreCase = true)

			// Earlier validation message (rare mid-round):
			val mustRegister = msg.contains("registrer navn og kortnummer", ignoreCase = true)

			when {
				won -> {
					// Round over: disable guessing; user can press Start
					setGuessEnabled(false)
				}
				outOfTries || mustRegister -> {
					// 3 attempts used / must re-register: disable guessing and clean guess area
					clearAfterThreeTries()
				}
				else -> {
					// Still playing: keep guessing enabled
					setGuessEnabled(true)
				}
			}

			setUiEnabled(true)
		}
	}

	private fun clearAfterThreeTries() {
		// Wipe guess & log but KEEP name/card
		textLog.text = ""
		inputGuess.setText("")
		setGuessEnabled(false)
		scrollRoot.post { scrollRoot.scrollTo(0, 0) }
	}

	private fun setUiEnabled(enabled: Boolean) {
		btnStart.isEnabled = enabled
		btnSend.isEnabled = enabled && inputGuess.isEnabled
		inputName.isEnabled = enabled
		inputCard.isEnabled = enabled
	}

	private fun setGuessEnabled(enabled: Boolean) {
		inputGuess.isEnabled = enabled
		btnSend.isEnabled = enabled
	}

	private fun setStatus(msg: String) {
		textStatus.text = msg
	}

	private fun appendLog(msg: String) {
		val existing = textLog.text?.toString().orEmpty()
		val newText = (existing + "\n" + msg).trim()
		textLog.text = newText

		scrollRoot.post {
			scrollRoot.fullScroll(View.FOCUS_DOWN)
		}
	}
}