package ntnu.leksjon_07.managers

import android.content.Context
import ntnu.leksjon_07.R
import ntnu.leksjon_07.service.Database
import org.json.JSONArray
import java.io.File
import java.io.PrintWriter

class FileManager(private val context: Context, private val db: Database) {

	fun initFromRawIfNeededAndWriteCopy() {
		if (!db.hasData()) {
			val raw = context.resources.openRawResource(R.raw.movies)
				.bufferedReader().use { it.readText() }
			val arr = JSONArray(raw)
			for (i in 0 until arr.length()) {
				val obj = arr.getJSONObject(i)
				val title = obj.getString("title")
				val director = obj.getString("director")
				val actorsJson = obj.getJSONArray("actors")
				val actors = MutableList(actorsJson.length()) { j -> actorsJson.getString(j) }
				db.insertMovie(title, director, actors)
			}
		}
		writeLocalCopy()
	}

	private fun writeLocalCopy() {
		val out = File(context.filesDir, "movies_imported.json")
		PrintWriter(out).use { it.println(exportJson()) }
	}

	private fun exportJson(): String {
		val titles = db.allMovies
		val map = db.allMoviesAndDirectors.associate {
			val parts = it.split(" — ")
			if (parts.size == 2) parts[0] to parts[1] else (it to "Ukjent")
		}
		val sb = StringBuilder("[")
		for ((idx, t) in titles.withIndex()) {
			val actors = db.getActorsByMovie(t)
			sb.append("{\"title\":\"").append(esc(t))
				.append("\",\"director\":\"").append(esc(map[t] ?: "Ukjent"))
				.append("\",\"actors\":[")
			for (i in actors.indices) {
				if (i > 0) sb.append(",")
				sb.append("\"").append(esc(actors[i])).append("\"")
			}
			sb.append("]}")
			if (idx < titles.size - 1) sb.append(",")
		}
		sb.append("]")
		return sb.toString()
	}

	private fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")
}
