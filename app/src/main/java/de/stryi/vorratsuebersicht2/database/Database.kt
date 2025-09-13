package de.stryi.vorratsuebersicht2.database

import android.content.ContentValues
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

    fun insertArtice(article: Article): Long {

        val values = ContentValues()
        values.put("Name",            article.name)
        values.put("Manufacturer",    article.manufacturer)
        values.put("Category",        article.category)
        values.put("SubCategory",     article.subCategory)
        values.put("DurableInfinity", article.durableInfinity)
        values.put("WarnInDays",      article.warnInDays)
        values.put("Size",            article.size)
        values.put("Unit",            article.unit)
        values.put("Notes",           article.notes)
        values.put("EANCode",         article.eanCode)
        values.put("Calorie",         article.calorie)
        values.put("Price",           article.price)
        values.put("StorageName",     article.storageName)
        values.put("Supermarket",     article.supermarket)

        val newId = db.insert("Article", null, values)

        return newId
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


    fun deleteArticle(articleId: Int) {
        val query = """
            DELETE FROM Article
            WHERE ArticleId = ?
        """.trimIndent()
        db.execSQL(query, arrayOf(articleId.toString()))
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

    fun getArticleName(articleId: Int): String {
        val query = """
            SELECT Name
            FROM Article
            WHERE ArticleId = ?
            """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(articleId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        return ""
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

        var lastCategory = ""
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

                    stringList.add(String.format("  - %s", subCategoryName))
                }
            }
        }
        return stringList
    }

    fun getManufacturerNames(): MutableList<String>
    {
        val query = """
            SELECT DISTINCT Manufacturer
            FROM Article
            WHERE Manufacturer IS NOT NULL
            AND Manufacturer <> ''
            ORDER BY Manufacturer COLLATE NOCASE
        """
        val result = db.queryStringList(query.trimIndent(), null)

        return result
    }

    fun SQLiteDatabase.queryStringList(
        query: String,
        args: Array<String>? = null
    ): MutableList<String> {
        val result = mutableListOf<String>()
        val cursor = this.rawQuery(query, args)
        cursor.use {
            while (it.moveToNext()) {
                result.add(it.getString(0))
            }
        }
        return result
    }

    fun getSubcategoriesOf(category: String? = null): MutableList<String>
    {
        val parameters = mutableListOf<String>()

        var query = """
            SELECT DISTINCT SubCategory
            FROM Article
            WHERE SubCategory IS NOT NULL
            AND SubCategory <> ''
        """
        if (category != null)
        {
            query += " AND Category = ?"
            parameters.add(category)
        }
        query += " ORDER BY SubCategory COLLATE NOCASE"

        val result = db.queryStringList(
            query.trimIndent(),
            parameters.toTypedArray())

        return result
    }

    fun getStorageNames(inStorageArticlesOnly: Boolean = false): MutableList<String>
    {
        var query = """
            SELECT DISTINCT Article.StorageName
            FROM Article
            WHERE Article.StorageName IS NOT NULL AND Article.StorageName <> ''
        """

        if (inStorageArticlesOnly)
        {
            query += """
                AND Article.ArticleId IN (
                    SELECT StorageItem.ArticleId
                    FROM StorageItem
                    WHERE StorageItem.StorageName IS NULL OR StorageItem.StorageName = ''
                )"""
        }

        query += """
            UNION
            SELECT StorageName AS Value
            FROM StorageItem
            WHERE StorageName IS NOT NULL AND StorageName <> ''
            ORDER BY 1 COLLATE NOCASE
        """
        val result = db.queryStringList(query.trimIndent(), null)

        return result
    }

    fun getSupermarketNames(shoppingListOnly: Boolean = false): MutableList<String>
    {
        var query = """
            SELECT DISTINCT Supermarket
            FROM Article
        """
        if (shoppingListOnly)
        {
            query += " JOIN ShoppingList ON ShoppingList.ArticleId = Article.ArticleId"
        }
        query += """
            WHERE Supermarket IS NOT NULL
            AND Supermarket <> ''
            ORDER BY Supermarket COLLATE NOCASE
        """

        var result = db.queryStringList(query.trimIndent(), null)

        val stringList = mutableListOf<String>()

        for (item in result) {
            val supermarketName = item

            if (!shoppingListOnly) {
                stringList.add(supermarketName)
                continue
            }

            supermarketName.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() && !stringList.contains(it) }
                .forEach { stringList.add(it) }
        }

        return result
    }

    fun insertArticleImage(articleImage: ArticleImage) {
        val query = """
            INSERT INTO ArticleImage (ArticleId, Type, CreatedAt, ImageLarge, ImageSmall)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()
        db.execSQL(query, arrayOf(articleImage.articleId, articleImage.type, articleImage.createdAt,
            articleImage.imageLarge, articleImage.imageSmall))
    }

    fun updateArticleImage(articleImage: ArticleImage) {
        val query = """
            UPDATE ArticleImage
            SET ImageLarge = ?, ImageSmall = ?
            WHERE ImageId = ?
        """.trimIndent()
        db.execSQL(query, arrayOf(articleImage.imageLarge, articleImage.imageSmall, articleImage.imageId))
    }

    fun deleteArticleImage(articleImage: ArticleImage) {
        val query = """
            DELETE FROM ArticleImage
            WHERE ImageId = ?
        """.trimIndent()
        db.execSQL(query, arrayOf(articleImage.imageId))
    }

    fun getArticleImage(articleId: Int?, showLarge: Boolean?  = null): ArticleImage?
    {
        var cmd = "SELECT ImageId, ArticleId, Type, CreatedAt"
        if (showLarge == null) {
            cmd += ", ImageLarge, ImageSmall"
        }
        else
        {
            if (showLarge == true)
                cmd += ", ImageLarge"
            else
                cmd += ", ImageSmall"
        }

        cmd += " FROM ArticleImage"
        cmd += " WHERE ArticleId = ?"
        cmd += " AND Type = 0"

        var result: ArticleImage? = null
        val cursor = db.rawQuery(cmd, arrayOf(articleId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                result = ArticleImage.fromCursor(it)
            }
        }

        return result
    }


}
