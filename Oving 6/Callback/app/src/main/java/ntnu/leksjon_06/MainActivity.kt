package ntnu.leksjon_06

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
	}

	override fun onStart() {
		super.onStart()

		// Create function as callback directly
		NumberHandler.getNumbersEverySecond { number: Int ->
			MainScope().launch {
				findViewById<TextView>(R.id.counter1).text = "$number"
			}
		}

		// Pass function with correct signature
		NumberHandler.getNumbersEverySecond(::updateCounter)

		// Same as first example but now we have to return a String
		NumberHandler.getNumbersEverySecond2 { number: Int ->
			MainScope().launch {
				findViewById<TextView>(R.id.counter1).text = "$number"
			}
			"Thanks for the number <3" // Return value from callback
		}
	}

	private fun updateCounter(number: Int) {
		MainScope().launch {
			findViewById<TextView>(R.id.counter2).text = "$number"
		}
	}
}
