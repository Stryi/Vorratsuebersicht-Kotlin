import android.database.Cursor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Cursor.getStringOrNull2(columnName: String): String?
{
    val index = this.getColumnIndex(columnName);
    if (index < 0) { return null }

    return this.getString(index)
}

fun Cursor.getStringOrNull(columnName: String): String? =
    getColumnIndexOrNull(columnName)?.takeIf { !isNull(it) }?.let { getString(it) }

fun Cursor.getIntOrNull(columnName: String): Int? =
    getColumnIndexOrNull(columnName)?.takeIf { !isNull(it) }?.let { getInt(it) }

fun Cursor.getDoubleOrNull(columnName: String): Double? =
    getColumnIndexOrNull(columnName)?.takeIf { !isNull(it) }?.let { getDouble(it) }

fun Cursor.getDateOrNull(columnName: String): Date?
    {
        val index = getColumnIndexOrNull(columnName) ?: return null
        if (isNull(index)) return null

        val dateStr = getString(index)
        if (dateStr.isBlank())
        {
            return null
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try {
            return sdf.parse(dateStr)
        } catch (e: Exception) {
        }
        return null
    }

fun Cursor.getBlobOrNull(columnName: String): ByteArray?
{
    val index = this.getColumnIndex(columnName)
    if (index < 0) { return null }
    return this.getBlob(index)
}

fun Cursor.getColumnIndexOrNull(columnName: String): Int? =
    try {
        getColumnIndexOrThrow(columnName)
    } catch (e: IllegalArgumentException) {
        null
    }
