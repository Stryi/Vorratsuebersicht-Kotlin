package de.stryi.vorratsuebersicht2.database

import android.database.Cursor
import getBlobOrNull
import getDateOrNull
import getIntOrNull
import java.util.Date

class ArticleImage {

    var imageId: Int = 0
    var articleId: Int = 0
    var type: Int = 0
    var createdAt: Date? = null
    var imageSmall: ByteArray? = null
    var imageLarge: ByteArray? = null

    companion object {
        fun fromCursor(cursor: Cursor): ArticleImage {
            val articleImage = ArticleImage()
            articleImage.imageId    = cursor.getIntOrNull("ImageId") ?: 0
            articleImage.articleId  = cursor.getIntOrNull("ArticleId") ?: 0
            articleImage.type       = cursor.getIntOrNull("Type") ?: 0
            articleImage.createdAt  = cursor.getDateOrNull("CreatedAt")
            articleImage.imageSmall = cursor.getBlobOrNull("ImageSmall")
            articleImage.imageLarge = cursor.getBlobOrNull("ImageLarge")
            return articleImage
        }
    }
}