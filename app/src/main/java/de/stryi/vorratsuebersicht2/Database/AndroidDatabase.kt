package de.stryi.vorratsuebersicht2

import android.content.Context

object AndroidDatabase {

    const val SQLITE_FILENAME_PROD = "Vorraete.db3"
    const val SQLITE_FILENAME_NEW  = "Vorraete_db0.db3"
    const val SQLITE_FILENAME_DEMO = "Vorraete_Demo.db3"
    const val SQLITE_FILENAME_TEST = "Vorraete_Test.db3"

    /// <summary>
    /// Datenbanken aus den Resourcen erstellen.
    /// </summary>
    fun restoreDatabasesFromResourcesOnStartup(context: Context)
    {
        // Localized demo database with sample data.
        createLocalizedDatabaseFromAsset(context, SQLITE_FILENAME_PROD)
    }

    /// <summary>
    /// Erstellt Datenbank aus den Resourcen.
    /// </summary>
    private fun createLocalizedDatabaseFromAsset(context: Context, fileName: String)
    {
        val dbPath = context.getDatabasePath(fileName)

        if (dbPath.exists())
            return

        dbPath.parentFile?.mkdirs()

        context.assets.open(fileName).use { inputStream ->
            dbPath.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}