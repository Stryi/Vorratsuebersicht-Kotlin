import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class MyDatabase(context: Context) : SQLiteOpenHelper(
    context, "Vorraete_Demo.db3", null, 1)
{
    override fun onCreate(p0: SQLiteDatabase?) {  }
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {  }
}

object Database
{
    fun GetArticleList(context: Context): List<String> {

        val databaseConnection = MyDatabase(context)

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
