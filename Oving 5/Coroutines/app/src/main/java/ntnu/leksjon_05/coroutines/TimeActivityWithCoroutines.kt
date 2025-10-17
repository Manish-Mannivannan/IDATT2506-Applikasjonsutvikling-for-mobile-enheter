package ntnu.leksjon_05.coroutines

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ntnu.leksjon_05.coroutines.Util.Companion.currentTime

class TimeActivityWithCoroutines : AppCompatActivity() {

	private lateinit var clockText: TextView
	private lateinit var smsText: TextView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		clockText = findViewById(R.id.clock)
		smsText = findViewById(R.id.sms)
	}

	override fun onStart() {
		super.onStart()

		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				// Clock job
				launch(Dispatchers.Default) {
					while (isActive) {
						val now = "Nåværende tid: ${currentTime()}"
						runOnUiThread { clockText.text = now }
						delay(1000)
					}
				}
				// SMS job
				launch(Dispatchers.Default) {
					var counter = 1
					while (isActive) {
						runOnUiThread { smsText.text = "Sender SMS $counter..." }
						delay(5000)
						runOnUiThread { smsText.text = "Fullført SMS $counter" }
						counter++
						delay(250)
					}
				}
			}
		}
	}
}
