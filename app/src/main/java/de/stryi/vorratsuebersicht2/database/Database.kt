package de.stryi.vorratsuebersicht2.database

import android.R
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

    fun getArticleList(
        category: String,
        subCategory: String,
        eanCode: String?,
        notInStorage: Boolean,
        notInShoppingList: Boolean,
        withoutCategory: Boolean,
        specialFilter: Int,
        textFilter: String?
    ): List<Article> {

        val parameter = mutableListOf<String>()

        var filter = ""

        if (category.isNotEmpty())
        {
            filter += " WHERE Article.Category = ?"
            parameter.add(category)
        }

        if (subCategory.isNotEmpty())
        {
            if (filter.isNotEmpty())
                filter += " AND "
            else
                filter += " WHERE "

            filter += " Article.SubCategory = ?"
            parameter.add(subCategory)
        }

        if (withoutCategory)
        {
            if (filter.isNotEmpty())
                filter += " AND "
            else
                filter += " WHERE "

            filter += " (Article.Category IS NULL OR Article.Category = ''" +
                    " OR Article.SubCategory IS NULL OR Article.SubCategory = '') "
        }

        if (!eanCode.isNullOrEmpty())
        {
            if (filter.isNotEmpty())
                filter += " AND "
            else
                filter += " WHERE "

            filter += " Article.EANCode LIKE ?"
            parameter.add("%$eanCode%")
        }

        if (!textFilter.isNullOrEmpty())
        {
            if (filter.isNotEmpty()) filter += " AND "
            else filter += " WHERE "

            when (textFilter.uppercase()) {
                "P-" -> filter += " Article.Price IS NULL"
                "K-" -> filter += " Article.Calorie IS NULL"
                "B+" -> {
                    // Artikel zum [B]estellen.
                    filter += " ArticleId NOT IN (SELECT ArticleId FROM ShoppingList)"
                    filter += " AND "
                    filter += " ArticleId NOT IN (SELECT ArticleId FROM StorageItem)"
                }

                else -> {
                    filter += " (Article.Name LIKE ? OR Article.Manufacturer LIKE ? OR Article.Notes LIKE ? OR Article.Supermarket LIKE ?"
                    filter += " OR Article.StorageName LIKE ? OR Article.Category LIKE ? OR Article.SubCategory LIKE ? OR Article.EANCode LIKE ?)"
                    parameter.add("%$textFilter%")
                    parameter.add("%$textFilter%")
                    parameter.add("%$textFilter%")
                    parameter.add("%$textFilter%")
                    parameter.add("%$textFilter%")
                    parameter.add("%$textFilter%")
                    parameter.add("%$textFilter%")
                    parameter.add("%$textFilter%")
                }
            }
        }

        if (notInStorage)
        {
            if (filter.isNotEmpty())
                filter += " AND "
            else
                filter += " WHERE "

            filter += "ArticleId NOT IN (SELECT ArticleId FROM StorageItem)"
        }

        if (notInShoppingList)
        {
            if (filter.isNotEmpty())
                filter += " AND "
            else
                filter += " WHERE "

            filter += "ArticleId NOT IN (SELECT ArticleId FROM ShoppingList)"
        }

        if (specialFilter > 0)
        {
            if (filter.isNotEmpty()) filter += " AND "
            else filter += " WHERE "

            when (specialFilter) {
                1 -> filter += " Article.Price IS NULL"
                2 -> filter += " Article.Calorie IS NULL"
                3 -> {
                    // Artikel zum [B]estellen.
                    filter += " ArticleId NOT IN (SELECT ArticleId FROM ShoppingList)"
                    filter += " AND "
                    filter += " ArticleId NOT IN (SELECT ArticleId FROM StorageItem)"
                }

                4 -> filter += " ((Article.StorageName IS NULL) OR (Article.StorageName == ''))"
            }
        }


        val query = """
            SELECT ArticleId, Name, Manufacturer, Category, SubCategory, DurableInfinity, WarnInDays,
                   Size, Unit, Notes, EANCode, Calorie, Price, StorageName, Supermarket
            FROM Article
            $filter
            ORDER BY Name COLLATE NOCASE
        """.trimIndent()

        val result = mutableListOf<Article>()
        val cursor = db.rawQuery(query, parameter.toTypedArray())
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

    fun getCategoryAndSubcategoryNames(): MutableList<String> {
        val query = """
            SELECT DISTINCT Category, SubCategory
            FROM Article
            WHERE Category IS NOT NULL
            ORDER BY Category COLLATE NOCASE, SubCategory COLLATE NOCASE
        """.trimIndent()
        val cursor = db.rawQuery(query, null)

        var lastCategory: String = ""
        val stringList: MutableList<String> = mutableListOf()

        cursor.use {
            while (it.moveToNext()) {
                val article = Article.fromCursor(it)
                val categoryName = article.category
                val subCategoryName = article.subCategory

                if ((categoryName == null) && (subCategoryName == null))
                    continue

                if (categoryName != lastCategory)
                {
                    stringList.add(categoryName.toString())
                    lastCategory = categoryName.toString()
                }

                if (!subCategoryName.isNullOrBlank())
                {
                    // Die Zeichenfülge "  - " vor dem {0} ist wichtig
                    // für das Erkennen der Unterkategorie bei Auswahl.

                    stringList.add(String.format("  - %s", subCategoryName));
                }
            }
        }
        return stringList
    }

    fun getManufacturerNames(): MutableList<String> {
        val query = """
            SELECT DISTINCT Manufacturer
            FROM Article
            WHERE Manufacturer IS NOT NULL
            AND Manufacturer <> ''
            ORDER BY StorageName COLLATE NOCASE
            
            """.trimIndent()
        val cursor = db.rawQuery(query, null)
        val stringList: MutableList<String> = mutableListOf()
        cursor.use {
            while (it.moveToNext()) {
                stringList.add(it.getString(0))
            }
        }
        return stringList
    }
}
