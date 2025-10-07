package ntnu.leksjon_02

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MathActivity : Activity() {

    private lateinit var tvA: TextView
    private lateinit var tvB: TextView
    private lateinit var etSvar: EditText
    private lateinit var etMax: EditText

    private val REQ_A = 201
    private val REQ_B = 202

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math)

        tvA = findViewById(R.id.tvA)
        tvB = findViewById(R.id.tvB)
        etSvar = findViewById(R.id.etSvar)
        etMax = findViewById(R.id.etMax)
    }

    // Button: "Adder"
    fun onClickAdder(@Suppress("UNUSED_PARAMETER") v: View) {
        val a = tvA.text.toString().toIntOrNull() ?: 0
        val b = tvB.text.toString().toIntOrNull() ?: 0
        val bruker = etSvar.text.toString().toIntOrNull()
        val fasit = a + b
        showResult(bruker, fasit)
        requestRandomForAThenB()
    }

    // Button: "Multipliser"
    fun onClickMultipliser(@Suppress("UNUSED_PARAMETER") v: View) {
        val a = tvA.text.toString().toIntOrNull() ?: 0
        val b = tvB.text.toString().toIntOrNull() ?: 0
        val bruker = etSvar.text.toString().toIntOrNull()
        val fasit = a * b
        showResult(bruker, fasit)
        requestRandomForAThenB()
    }

    private fun showResult(bruker: Int?, fasit: Int) {
        if (bruker != null && bruker == fasit) {
            Toast.makeText(this, getString(R.string.riktig), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                "${getString(R.string.feil_riktig_er)} $fasit",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Use RandomNumberActivity twice to refresh A then B
    private fun requestRandomForAThenB() {
        val max = etMax.text.toString().toIntOrNull() ?: 10
        val iA = Intent("ntnu.leksjon_02.action.RANDOM_NUMBER")
            .putExtra(RandomNumberActivity.EXTRA_MAX, max)
        startActivityForResult(iA, REQ_A)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) return

        val value = data.getIntExtra(RandomNumberActivity.EXTRA_VALUE, 0)
        if (requestCode == REQ_A) {
            tvA.text = value.toString()
            val max = etMax.text.toString().toIntOrNull() ?: 10
            val iB = Intent("ntnu.leksjon_02.action.RANDOM_NUMBER")
                .putExtra(RandomNumberActivity.EXTRA_MAX, max)
            startActivityForResult(iB, REQ_B)
        } else if (requestCode == REQ_B) {
            tvB.text = value.toString()
        }
    }
}
