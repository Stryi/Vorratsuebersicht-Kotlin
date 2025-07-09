package de.stryi.vorratsuebersicht2

import android.database.Cursor
import getDoubleOrNull
import getIntOrNull
import getStringOrNull

class Article {
    var articleId: Int = 0
    var name: String? = null
    var manufacturer: String? = null
    var category: String? = null
    var subCategory: String? = null
    var durableInfinity: Boolean = false
    var warnInDays: Int? = null
    var size: Double? = null
    var unit: String? = null
    var calorie: Int? = null
    var notes: String? = null
    var eanCode: String? = null
    var storageName: String? = null
    var minQuantity: Int? = null
    var prefQuantity: Int? = null
    var supermarket: String? = null
    var price: Double? = null

    companion object {
        fun fromCursor(cursor: Cursor): Article {
            val article = Article()
            article.articleId = cursor.getIntOrNull("ArticleId") ?: 0
            article.eanCode = cursor.getStringOrNull("EANCode")
            article.name = cursor.getStringOrNull("Name")
            article.manufacturer = cursor.getStringOrNull("Manufacturer")
            article.category = cursor.getStringOrNull("Category")
            article.subCategory = cursor.getStringOrNull("SubCategory")
            article.durableInfinity = cursor.getIntOrNull("DurableInfinity") == 1
            article.warnInDays = cursor.getIntOrNull("WarnInDays")
            article.size = cursor.getDoubleOrNull("Size")
            article.unit = cursor.getStringOrNull("Unit")
            article.calorie = cursor.getIntOrNull("Calorie")
            article.notes = cursor.getStringOrNull("Notes")
            article.storageName = cursor.getStringOrNull("StorageName")
            article.minQuantity = cursor.getIntOrNull("MinQuantity")
            article.prefQuantity = cursor.getIntOrNull("PrefQuantity")
            article.supermarket = cursor.getStringOrNull("Supermarket")
            article.price = cursor.getDoubleOrNull("Price")
            return article
        }
    }
}
