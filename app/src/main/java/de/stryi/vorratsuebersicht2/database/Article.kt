package de.stryi.vorratsuebersicht2.database

import android.database.Cursor
import de.stryi.vorratsuebersicht2.tools.PricePerUnit
import de.stryi.vorratsuebersicht2.tools.UnitConvert
import getDoubleOrNull
import getIntOrNull
import getStringOrNull
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

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
            article.articleId    = cursor.getIntOrNull("ArticleId") ?: 0
            article.eanCode      = cursor.getStringOrNull("EANCode")
            article.name         = cursor.getStringOrNull("Name")
            article.manufacturer = cursor.getStringOrNull("Manufacturer")
            article.category     = cursor.getStringOrNull("Category")
            article.subCategory  = cursor.getStringOrNull("SubCategory")
            article.durableInfinity = cursor.getIntOrNull("DurableInfinity") == 1
            article.warnInDays   = cursor.getIntOrNull("WarnInDays")
            article.size         = cursor.getDoubleOrNull("Size")
            article.unit         = cursor.getStringOrNull("Unit")
            article.calorie      = cursor.getIntOrNull("Calorie")
            article.notes        = cursor.getStringOrNull("Notes")
            article.storageName  = cursor.getStringOrNull("StorageName")
            article.minQuantity  = cursor.getIntOrNull("MinQuantity")
            article.prefQuantity = cursor.getIntOrNull("PrefQuantity")
            article.supermarket  = cursor.getStringOrNull("Supermarket")
            article.price        = cursor.getDoubleOrNull("Price")
            return article
        }
    }

    val heading: String
        get() {
            return this.name ?: ""
        }

    val subHeading: String
        get() {
            val info = StringBuilder()

            // Hersteller
            this.manufacturer?.takeIf { it.isNotBlank() }?.let {
                info.appendLine("Hersteller: $it")
            }

            // Kategorie / Unterkategorie
            val categoryText = buildString {
                if (!category.isNullOrBlank()) append(category)
                if (!subCategory.isNullOrBlank()) {
                    if (isNotEmpty()) append(" / ")
                    append(subCategory)
                }
            }
            if (categoryText.isNotBlank()) {
                info.appendLine("Kategorie: $categoryText")
            }

            // Supermarkt
            supermarket?.takeIf { it.isNotBlank() }?.let {
                info.appendLine("Einkaufsmarkt: $it")
            }

            // Lagerort
            storageName?.takeIf { it.isNotBlank() }?.let {
                info.appendLine("Standard Lagerort: $it")
            }

            // Warnung in Tagen
            if (!durableInfinity && warnInDays != null) {
                info.appendLine("Warnen in Tagen vor Ablauf: $warnInDays")
            }

            // Preis
            if (price != null) {
                val priceText = "%.2f".format(price)
                info.append("Preis: ")
                info.append(" $priceText")

                val pricePerUnit = PricePerUnit.calculate(price, size, unit)
                if (pricePerUnit.isNotBlank()) {
                    info.append(" ($pricePerUnit)")
                }
            }

            // Größe
            if (size != null) {
                val sizeText = "%.0f".format(size)
                info.appendLine()
                info.append("Inhalt/Größe: $sizeText ${unit ?: ""}".trimEnd())
            }

            // Kalorien
            if (calorie != null) {
                val calorieText = "%.0f".format(calorie?.toDouble() ?: 0.0)
                info.appendLine()
                info.append("Kalorien:")
                info.append(" $calorieText")

                if (calorie != 0) {
                    val unitPerX = UnitConvert.getConvertUnit(unit)
                    val calPerUnit = UnitConvert.getCaloriePerUnit(
                        size?.toString(),
                        unit,
                        calorie?.toString()
                    )
                    if (calPerUnit != "---") {
                        info.append(" (100 $unitPerX = $calPerUnit)")
                    }
                }
            }
            return info.toString().trimEnd()
        }

    val notesText: String
        get() {
            val info = StringBuilder()

            // Hersteller
            this.notes?.takeIf { it.isNotBlank() }?.let {
                info.appendLine("Notizen: $it")
            }

            return info.toString().trimEnd()
        }

    var shoppingQuantityCache: Double? = null

    val isOnShoppingList: Boolean
        get() {
            if (shoppingQuantityCache == null)
            {
                shoppingQuantityCache = Database.getShoppingListQuantiy(
                    this.articleId,
                    (-1).toDouble()
                )
            }
            return shoppingQuantityCache!! >= 0.0  // Menge 0 bedeutet: Auf EInkaufszettel, aber ohne Menge.
        }

    val shoppingQuantity: String
        get() {
            if (!isOnShoppingList)
                return ""

            if (shoppingQuantityCache == 0.toDouble())
                return ""

            val locale = Locale.getDefault()
            val symbols = DecimalFormatSymbols(locale)
            val formatter = DecimalFormat("#,0.######", symbols)

            return formatter.format(shoppingQuantityCache)
        }

    var storageQuantityCache: Double? = null

    val isInStorage: Boolean
        get() {
            if (storageQuantityCache == null)
            {
                storageQuantityCache = Database.getArticleQuantityInStorage(this.articleId)
            }
            return storageQuantityCache!! > 0.0
        }

    val storageQuantity: String
        get() {
            if (!isInStorage)
                return "0"

            val locale = Locale.getDefault()
            val symbols = DecimalFormatSymbols(locale)
            val formatter = DecimalFormat("#,0.######", symbols)

            return formatter.format(storageQuantityCache)
        }
}
