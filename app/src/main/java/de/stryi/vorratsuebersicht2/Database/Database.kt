import android.R.string
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import de.stryi.vorratsuebersicht2.Article


object Database
{
    private lateinit var db: SQLiteDatabase

    fun init(context: Context, fileName: String) {
        val dbFile = context.getDatabasePath(fileName)
        db = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    fun getArticleList(): List<Article> {

        val filter = ""

        val query = """
            SELECT ArticleId, Name, Manufacturer, Category, SubCategory, DurableInfinity, WarnInDays,
                   Size, Unit, Notes, EANCode, Calorie, Price, StorageName, Supermarket
            FROM Article
            $filter
            ORDER BY Name COLLATE NOCASE
        """.trimIndent()

        val result = mutableListOf<Article>()
        val cursor = db.rawQuery(query, null)
        cursor.use {
            while (it.moveToNext()) {
                val article = Article.fromCursor(it)
                result.add(article)
            }
        }
        return result
    }

    fun getArticlesByEanCode(eanCode: String): List<String>
    {
        val result = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT name FROM article WHERE eanCode = ?",
            arrayOf(eanCode))
        cursor.use {
            while (it.moveToNext()) {
                result.add(it.getString(0))
            }
        }
        return result
    }

    fun GetArticleImage(articleId: Int? , showLarge: Boolean?  = null): ByteArray
    {
        val result = mutableListOf<ByteArray>()
        val cmd = """
            SELECT ImageId, ArticleId, Type, ImageSmall
            FROM ArticleImage
            WHERE ArticleId = ?
            AND Type = 0
        """.trimIndent()
        val cursor = db.rawQuery(cmd, arrayOf(articleId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                result.add(it.getBlob(3))
            }
        }
        if (result.isEmpty())
            return ByteArray(0)

        return result[0]
    }
}
