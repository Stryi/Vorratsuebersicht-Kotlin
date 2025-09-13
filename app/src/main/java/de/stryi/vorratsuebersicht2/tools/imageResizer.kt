package de.stryi.vorratsuebersicht2.tools

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object imageResizer {

    fun resizeImageAndroid(originalImage: Bitmap, width: Float, height: Float): ByteArray {
        val hoehe = originalImage.height.toFloat()
        val breite = originalImage.width.toFloat()

        val zielHoehe: Float
        val zielBreite: Float

        if (hoehe > breite) {
            // Höhe ist Master
            zielHoehe = height
            val teiler = hoehe / height
            zielBreite = breite / teiler
        } else {
            // Breite ist Master
            zielBreite = width
            val teiler = breite / width
            zielHoehe = hoehe / teiler
        }

        val resizedImage = Bitmap.createScaledBitmap(
            originalImage,
            zielBreite.toInt(),
            zielHoehe.toInt(),
            false
        )

        val stream = ByteArrayOutputStream()
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}
