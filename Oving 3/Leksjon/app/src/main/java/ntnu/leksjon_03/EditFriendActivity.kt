package ntnu.leksjon_03

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import java.util.Calendar

class EditFriendActivity : Activity() {

    companion object {
        const val EXTRA_NAME = "name"
        const val EXTRA_YEAR = "year"
        const val EXTRA_MONTH = "month"
        const val EXTRA_DAY = "day"
    }

    private lateinit var etName: EditText
    private lateinit var dp: DatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_friend)

        etName = findViewById(R.id.etName)
        dp = findViewById(R.id.datePicker)

        // Prefill if editing
        val name = intent.getStringExtra(EXTRA_NAME)
        val y = intent.getIntExtra(EXTRA_YEAR, -1)
        val m = intent.getIntExtra(EXTRA_MONTH, -1)
        val d = intent.getIntExtra(EXTRA_DAY, -1)

        if (name != null) etName.setText(name)
        if (y > 0 && m > 0 && d > 0) {
            dp.updateDate(y, m - 1, d) // DatePicker uses 0-based month
        } else {
            val c = Calendar.getInstance()
            dp.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val out = Intent()
                .putExtra(EXTRA_NAME, etName.text.toString().trim())
                .putExtra(EXTRA_YEAR, dp.year)
                .putExtra(EXTRA_MONTH, dp.month + 1)
                .putExtra(EXTRA_DAY, dp.dayOfMonth)
            setResult(RESULT_OK, out)
            finish()
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
