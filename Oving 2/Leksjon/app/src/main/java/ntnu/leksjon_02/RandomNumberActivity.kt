package ntnu.leksjon_02

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlin.random.Random

class RandomNumberActivity : Activity() {

    companion object {
        const val EXTRA_MAX = "max"
        const val EXTRA_VALUE = "value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val upper = intent.getIntExtra(EXTRA_MAX, 100)
        val value = Random.nextInt(upper + 1)   // 0..upper inclusive

        // (a) originally you’d Toast here; for (d) keep it commented:
        //Toast.makeText(this, "Tilfeldig: $value", Toast.LENGTH_SHORT).show()

        // (c) return result to caller
        setResult(RESULT_OK, Intent().putExtra(EXTRA_VALUE, value))

        // (d) end immediately
        finish()
    }
}
