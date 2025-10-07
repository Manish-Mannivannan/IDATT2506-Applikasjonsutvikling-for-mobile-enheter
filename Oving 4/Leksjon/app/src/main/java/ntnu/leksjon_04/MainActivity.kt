package ntnu.leksjon_04

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), ListFragment.OnTitleClickListener {

	private lateinit var titles: Array<String>
	private lateinit var descriptions: Array<String>
	private lateinit var posters: IntArray

	private var currentIndex = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)   //system ActionBar from theme
		applyOrientationLayout()

		titles = resources.getStringArray(R.array.titles)
		descriptions = resources.getStringArray(R.array.descriptions)
		posters = resources.obtainTypedArray(R.array.posters).let { ta ->
			IntArray(ta.length()) { i -> ta.getResourceId(i, 0) }.also { ta.recycle() }
		}

		if (savedInstanceState == null) {
			supportFragmentManager.beginTransaction()
				.replace(R.id.listContainer, ListFragment.newInstance(titles.toList()))
				.replace(
					R.id.detailContainer,
					DetailFragment.newInstance(
						titles[currentIndex],
						descriptions[currentIndex],
						posters[currentIndex]
					)
				)
				.commit()
		}

		// One layout used for both orientations; adjust orientation/weights here
		val content = findViewById<LinearLayout>(R.id.content)
		val list = findViewById<FrameLayout>(R.id.listContainer)
		val detail = findViewById<FrameLayout>(R.id.detailContainer)

		val landscape =
			resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
		content.orientation = if (landscape) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
		(list.layoutParams as LinearLayout.LayoutParams).weight = 1f
		(detail.layoutParams as LinearLayout.LayoutParams).weight = if (landscape) 2f else 1f
		list.requestLayout(); detail.requestLayout()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_previous -> {
				currentIndex = (currentIndex - 1 + titles.size) % titles.size
				showDetail(currentIndex)
				return true
			}
			R.id.action_next -> {
				currentIndex = (currentIndex + 1) % titles.size
				showDetail(currentIndex)
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onTitleClicked(index: Int) {
		currentIndex = index
		showDetail(index)
	}

	private fun showDetail(index: Int) {
		val frag = DetailFragment.newInstance(
			titles[index],
			descriptions[index],
			posters[index]
		)
		supportFragmentManager.beginTransaction()
			.replace(R.id.detailContainer, frag)
			.commit()
	}
	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		applyOrientationLayout()
	}
	private fun applyOrientationLayout() {
		val content = findViewById<LinearLayout>(R.id.content)
		val list = findViewById<FrameLayout>(R.id.listContainer)
		val detail = findViewById<FrameLayout>(R.id.detailContainer)

		val landscape = resources.configuration.orientation ==
				Configuration.ORIENTATION_LANDSCAPE

		content.orientation = if (landscape) LinearLayout.HORIZONTAL
		else LinearLayout.VERTICAL

		val lpList = list.layoutParams as LinearLayout.LayoutParams
		val lpDetail = detail.layoutParams as LinearLayout.LayoutParams

		if (landscape) {
			// use width weights
			lpList.width = 0
			lpDetail.width = 0
			lpList.height = LinearLayout.LayoutParams.MATCH_PARENT
			lpDetail.height = LinearLayout.LayoutParams.MATCH_PARENT
			lpList.weight = 1f
			lpDetail.weight = 2f
		} else {
			// use height weights
			lpList.width = LinearLayout.LayoutParams.MATCH_PARENT
			lpDetail.width = LinearLayout.LayoutParams.MATCH_PARENT
			lpList.height = 0
			lpDetail.height = 0
			lpList.weight = 1f
			lpDetail.weight = 1f
		}

		list.layoutParams = lpList
		detail.layoutParams = lpDetail
	}
}
