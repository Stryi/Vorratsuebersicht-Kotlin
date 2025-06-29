import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, "Vorraete_Demo.db3", null, 1
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE benutzer (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS benutzer")
        onCreate(db)
    }

    fun insertBenutzer(name: String) {
        val values = ContentValues().apply {
            put("name", name)
        }
        writableDatabase.insert("benutzer", null, values)
    }

    fun getAlleBenutzer(): List<String> {
        val benutzer = mutableListOf<String>()
        val cursor: Cursor = readableDatabase.rawQuery("SELECT name FROM article", null)
        with(cursor) {
            while (moveToNext()) {
                benutzer.add(getString(0))
            }
            close()
        }
        return benutzer
    }
}
