package ntnu.leksjon_05.coroutines

import java.util.Calendar

class Util {
	companion object {

		/** Get and format time string from Calendar */
		fun currentTime(): String {
			val cal = Calendar.getInstance()
			val h = cal[Calendar.HOUR_OF_DAY]
			val m = cal[Calendar.MINUTE]
			val s = cal[Calendar.SECOND]
			return "${two(h)}:${two(m)}:${two(s)}"
		}

		private fun two(v: Int): String = if (v < 10) "0$v" else v.toString()
	}
}
