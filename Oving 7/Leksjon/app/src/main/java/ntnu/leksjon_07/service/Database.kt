package ntnu.leksjon_07.service

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.ArrayList

class Database(context: Context) : SQLiteOpenHelper(context, "movies.db", null, 1) {

	override fun onCreate(db: SQLiteDatabase) {
		db.execSQL("""
            CREATE TABLE DIRECTOR(
              _id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT UNIQUE NOT NULL
            );
        """.trimIndent())

		db.execSQL("""
            CREATE TABLE MOVIE(
              _id INTEGER PRIMARY KEY AUTOINCREMENT,
              title TEXT UNIQUE NOT NULL,
              director_id INTEGER NOT NULL,
              FOREIGN KEY(director_id) REFERENCES DIRECTOR(_id)
            );
        """.trimIndent())

		db.execSQL("""
            CREATE TABLE ACTOR(
              _id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT UNIQUE NOT NULL
            );
        """.trimIndent())

		db.execSQL("""
            CREATE TABLE MOVIE_ACTOR(
              _id INTEGER PRIMARY KEY AUTOINCREMENT,
              movie_id INTEGER NOT NULL,
              actor_id INTEGER NOT NULL,
              UNIQUE(movie_id, actor_id),
              FOREIGN KEY(movie_id) REFERENCES MOVIE(_id),
              FOREIGN KEY(actor_id) REFERENCES ACTOR(_id)
            );
        """.trimIndent())
	}

	override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
		db.execSQL("DROP TABLE IF EXISTS MOVIE_ACTOR")
		db.execSQL("DROP TABLE IF EXISTS MOVIE")
		db.execSQL("DROP TABLE IF EXISTS ACTOR")
		db.execSQL("DROP TABLE IF EXISTS DIRECTOR")
		onCreate(db)
	}

	fun hasData(): Boolean =
		readableDatabase.rawQuery("SELECT COUNT(*) FROM MOVIE", null).use { c ->
			c.moveToFirst(); c.getInt(0) > 0
		}

	private fun getOrCreateId(table: String, name: String): Long {
		val db = writableDatabase
		db.rawQuery("SELECT _id FROM $table WHERE name = ?", arrayOf(name)).use { c ->
			if (c.moveToFirst()) return c.getLong(0)
		}
		val cv = ContentValues().apply { put("name", name.trim()) }
		return db.insert(table, null, cv)
	}

	fun insertMovie(title: String, director: String, actors: List<String>) {
		val db = writableDatabase
		db.beginTransaction()
		try {
			val directorId = getOrCreateId("DIRECTOR", director)
			var movieId: Long? = null
			db.rawQuery("SELECT _id FROM MOVIE WHERE title = ?", arrayOf(title)).use { c ->
				if (c.moveToFirst()) movieId = c.getLong(0)
			}
			if (movieId == null) {
				val cvM = ContentValues().apply {
					put("title", title.trim())
					put("director_id", directorId)
				}
				movieId = db.insert("MOVIE", null, cvM)
			}
			for (a in actors) {
				val actorId = getOrCreateId("ACTOR", a)
				val cv = ContentValues().apply {
					put("movie_id", movieId)
					put("actor_id", actorId)
				}
				db.insertWithOnConflict("MOVIE_ACTOR", null, cv, SQLiteDatabase.CONFLICT_IGNORE)
			}
			db.setTransactionSuccessful()
		} finally {
			db.endTransaction()
		}
	}

	private fun queryStrings(sql: String, args: Array<String>? = null): ArrayList<String> {
		val out = ArrayList<String>()
		readableDatabase.rawQuery(sql, args).use { c ->
			while (c.moveToNext()) out.add(c.getString(0))
		}
		return out
	}

	val allMovies get() = queryStrings("SELECT title FROM MOVIE ORDER BY title")
	val allDirectors get() = queryStrings("SELECT name FROM DIRECTOR ORDER BY name")
	val allActors get() = queryStrings("SELECT name FROM ACTOR ORDER BY name")
	val allMoviesAndDirectors get() = queryStrings("""
        SELECT MOVIE.title || ' — ' || DIRECTOR.name
        FROM MOVIE JOIN DIRECTOR ON MOVIE.director_id = DIRECTOR._id
        ORDER BY MOVIE.title
    """.trimIndent())

	fun getMoviesByDirector(name: String) = queryStrings("""
        SELECT MOVIE.title
        FROM MOVIE JOIN DIRECTOR ON MOVIE.director_id = DIRECTOR._id
        WHERE DIRECTOR.name = ?
        ORDER BY MOVIE.title
    """.trimIndent(), arrayOf(name))

	fun getActorsByMovie(title: String) = queryStrings("""
        SELECT ACTOR.name
        FROM ACTOR
        JOIN MOVIE_ACTOR ON ACTOR._id = MOVIE_ACTOR.actor_id
        JOIN MOVIE ON MOVIE._id = MOVIE_ACTOR.movie_id
        WHERE MOVIE.title = ?
        ORDER BY ACTOR.name
    """.trimIndent(), arrayOf(title))

	fun getMoviesByActor(name: String) = queryStrings("""
        SELECT MOVIE.title
        FROM MOVIE
        JOIN MOVIE_ACTOR ON MOVIE._id = MOVIE_ACTOR.movie_id
        JOIN ACTOR ON ACTOR._id = MOVIE_ACTOR.actor_id
        WHERE ACTOR.name = ?
        ORDER BY MOVIE.title
    """.trimIndent(), arrayOf(name))
}
