package ntnu.leksjon_02

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : Activity() {

    private val REQ_RANDOM_MAIN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Button 1: generate a random number and show it in tvRandomResult
    fun onClickGenerateRandom(@Suppress("UNUSED_PARAMETER") v: View) {
        val intent = Intent("ntnu.leksjon_02.action.RANDOM_NUMBER")
            .putExtra(RandomNumberActivity.EXTRA_MAX, 100) // upper bound; change if you like
        startActivityForResult(intent, REQ_RANDOM_MAIN)
    }

    // Button 2: go to MathActivity
    fun onClickOpenMath(@Suppress("UNUSED_PARAMETER") v: View) {
        startActivity(Intent(this, MathActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) return
        if (requestCode == REQ_RANDOM_MAIN) {
            val value = data.getIntExtra(RandomNumberActivity.EXTRA_VALUE, -1)
            findViewById<TextView>(R.id.tvRandomResult).text = value.toString()
        }
    }
}
