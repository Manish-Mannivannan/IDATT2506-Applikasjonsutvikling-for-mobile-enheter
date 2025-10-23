package ntnu.leksjon_07

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import ntnu.leksjon_07.databinding.MinLayoutBinding
import ntnu.leksjon_07.managers.FileManager
import ntnu.leksjon_07.service.Database
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

	private lateinit var db: Database
	private lateinit var ui: MinLayoutBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		ui = MinLayoutBinding.inflate(layoutInflater)
		setContentView(ui.root)

		db = Database(this)

		// Seed DB from res/raw (if empty) and write a new local copy
		FileManager(this, db).initFromRawIfNeededAndWriteCopy()

		applyBackgroundColor()
	}

	override fun onResume() {
		super.onResume()
		applyBackgroundColor()
	}

	private fun applyBackgroundColor() {
		val prefs = PreferenceManager.getDefaultSharedPreferences(this)
		val colorValue = prefs.getString("bgColor", "#FFFFFF") ?: "#FFFFFF"
		ui.root.setBackgroundColor(Color.parseColor(colorValue))
	}

	private fun showResults(list: ArrayList<String>) {
		val res = StringBuffer("")
		for (s in list) res.append("$s\n")
		ui.result.text = res
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.settings, menu)
		menu.add(0, 1, 0, "Alle filmer")
		menu.add(0, 2, 0, "Alle regissører")
		menu.add(0, 3, 0, "Alle skuespillere")
		menu.add(0, 4, 0, "Alle filmer og regissører")
		menu.add(0, 5, 0, "Filmer av Christopher Nolan")
		menu.add(0, 6, 0, "Skuespillere i \"Inception\"")
		menu.add(0, 7, 0, "Filmer med Samuel L. Jackson")
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
			1 -> showResults(db.allMovies)
			2 -> showResults(db.allDirectors)
			3 -> showResults(db.allActors)
			4 -> showResults(db.allMoviesAndDirectors)
			5 -> showResults(db.getMoviesByDirector("Christopher Nolan"))
			6 -> showResults(db.getActorsByMovie("Inception"))
			7 -> showResults(db.getMoviesByActor("Samuel L. Jackson"))
			else -> return false
		}
		return true
	}
}
