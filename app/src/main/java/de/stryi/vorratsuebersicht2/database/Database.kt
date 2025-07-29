package de.stryi.vorratsuebersicht2.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import getDoubleOrNull


object Database
{
    private lateinit var db: SQLiteDatabase

    fun init(context: Context, fileName: String) {
        val dbFile = context.getDatabasePath(fileName)
        db = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    fun getArticleList(textFilter: String?): List<Article> {

        var filter = ""

        if (textFilter != null) {
            filter += "WHERE Name LIKE '%$textFilter%' OR Manufacturer LIKE '%$textFilter%' OR Category LIKE '%$textFilter%' OR SubCategory LIKE '%$textFilter%'"
        }

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

    fun getArticle(articleId: Int): Article? {
        val query = """
            SELECT *
            FROM Article
            WHERE ArticleId = ?
            """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(articleId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                return Article.fromCursor(it)
            }
        }
        return null
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

    fun updateArticle(article: Article)
    {
        val query = """
            UPDATE Article
            SET Name = ?, Manufacturer = ?, Category = ?, SubCategory = ?, DurableInfinity = ?, WarnInDays = ?,
                Size = ?, Unit = ?, Notes = ?, EANCode = ?, Calorie = ?, Price = ?, StorageName = ?, Supermarket = ?
            WHERE ArticleId = ?
        """.trimIndent()
        db.execSQL(query, arrayOf(article.name, article.manufacturer, article.category, article.subCategory,
            article.durableInfinity, article.warnInDays, article.size, article.unit, article.notes, article.eanCode,
            article.calorie, article.price, article.storageName, article.supermarket, article.articleId))
    }

    fun getShoppingListQuantiy(articleId: Int, notFoundDefault: Double?): Double?
    {
        val query = """
            SELECT Quantity
            FROM ShoppingList
            WHERE ArticleId = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(articleId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                return it.getDoubleOrNull("Quantity")
            }
        }
        return notFoundDefault
    }

    fun getArticleQuantityInStorage(articleId: Int): Double
    {
        val query = """
            SELECT SUM(Quantity) AS Quantity
            FROM StorageItem
            WHERE ArticleId = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(articleId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                return it.getDoubleOrNull("Quantity") ?: 0.0
            }
        }
        return 0.0
    }
}
