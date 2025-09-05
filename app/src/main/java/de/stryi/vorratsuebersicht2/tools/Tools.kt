package de.stryi.vorratsuebersicht2.tools

import java.util.Locale

object Tools {

    fun toFuzzyByteString(bytes: Int?): String {
        if (bytes == null)
        {
            return ""
        }

        var s = bytes.toDouble()
        val formats = arrayOf(
            "%,.0f bytes", "%,.2f KB",
            "%,.2f MB", "%,.2f GB", "%,.2f TB",
            "%,.2f PB", "%,.2f EB"
        )

        var i = 0
        while (i < formats.size - 1 && s >= 1024) {
            s = (100 * s / 1024).toInt() / 100.0  // Rundung auf 2 Nachkommastellen
            i++
        }

        return String.format(Locale.getDefault(), formats[i], s)
    }
}
