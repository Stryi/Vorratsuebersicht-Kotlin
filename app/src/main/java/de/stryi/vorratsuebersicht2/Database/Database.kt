import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import de.stryi.vorratsuebersicht2.Android_Database


object Database
{
    fun GetArticleList(context: Context): List<String> {

        val databaseConnection = Android_Database.GetConnection(context)

        val result = mutableListOf<String>()
        val cursor: Cursor = databaseConnection.readableDatabase.rawQuery("SELECT name FROM article", null)
        with(cursor) {
            while (moveToNext()) {
                result.add(getString(0))
            }
            close()
        }
        return result
    }
}
