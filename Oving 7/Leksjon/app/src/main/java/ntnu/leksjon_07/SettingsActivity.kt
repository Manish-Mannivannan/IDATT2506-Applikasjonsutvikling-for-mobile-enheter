package ntnu.leksjon_07

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ntnu.leksjon_07.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity(),
	Preference.SummaryProvider<ListPreference> {

	private lateinit var ui: ActivitySettingsBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		ui = ActivitySettingsBinding.inflate(layoutInflater)
		setContentView(ui.root)

		supportFragmentManager.beginTransaction()
			.replace(R.id.settings_container, SettingsFragment())
			.commit()

		ui.button.setOnClickListener { finish() }
	}

	override fun provideSummary(preference: ListPreference?): CharSequence {
		return when (preference?.key) {
			getString(R.string.bg_color_key) -> preference.entry ?: ""
			else -> ""
		}
	}

	class SettingsFragment : PreferenceFragmentCompat() {
		override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
			setPreferencesFromResource(R.xml.preference_screen, rootKey)
			findPreference<ListPreference>(getString(R.string.bg_color_key))?.summaryProvider =
				(activity as? SettingsActivity)
		}
	}
}
