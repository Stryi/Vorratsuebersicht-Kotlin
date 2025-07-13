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

    fun getArticleImage(articleId: Int? , showLarge: Boolean?  = null): ByteArray
    {
        var cmd = "SELECT ImageId, ArticleId, Type"
        cmd += if (showLarge == null)
            ", ImageLarge, ImageSmall"
        else {
            if (showLarge)
                ", ImageLarge"
            else
                ", ImageSmall"
        }

        cmd += " FROM ArticleImage"
        cmd += " WHERE ArticleId = ?"
        cmd += " AND Type = 0"

        val result = mutableListOf<ByteArray>()
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
