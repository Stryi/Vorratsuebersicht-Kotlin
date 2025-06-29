import android.content.Context
import android.database.sqlite.SQLiteDatabase

object Database
{
    private lateinit var db: SQLiteDatabase

    fun init(context: Context, fileName: String) {
        val dbFile = context.getDatabasePath(fileName)
        db = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    fun getArticleList(): List<String> {

        val result = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT name FROM article", null)
        cursor.use {
            while (it.moveToNext()) {
                result.add(it.getString(0))
            }
        }
        return result
    }
}
