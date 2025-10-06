package ntnu.leksjon_01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    private val firstName = "Manish"
    private val lastName = "Mannivannan"
    companion object {
        private const val TAG = "Leksjon"
        private const val MENU_FORNAVN = 1
        private const val MENU_ETTERNAVN = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, MENU_FORNAVN, Menu.NONE, firstName)
        menu.add(Menu.NONE, MENU_ETTERNAVN, Menu.NONE, lastName)

        Log.d(TAG, "Meny laget")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        MENU_FORNAVN -> {
            // Fornavn skal logges som advarsel
            Log.w(TAG, firstName)
            true
        }
        MENU_ETTERNAVN -> {
            // Etternavn skal logges som feilmelding
            Log.e(TAG, lastName)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}