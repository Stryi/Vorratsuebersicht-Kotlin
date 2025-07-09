import android.database.Cursor

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

fun Cursor.getColumnIndexOrNull(columnName: String): Int? =
    try {
        getColumnIndexOrThrow(columnName)
    } catch (e: IllegalArgumentException) {
        null
    }
