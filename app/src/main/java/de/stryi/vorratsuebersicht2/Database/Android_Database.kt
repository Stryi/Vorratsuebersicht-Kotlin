package de.stryi.vorratsuebersicht2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseAccess(context: Context, databaseName: String) : SQLiteOpenHelper(
    context, databaseName, null, 1)
{
    override fun onCreate(p0: SQLiteDatabase?) {  }
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {  }
}

object Android_Database {

    const val SQLITE_FILENAME_PROD = "Vorraete.db3"
    const val SQLITE_FILENAME_NEW  = "Vorraete_db0.db3"
    const val SQLITE_FILENAME_DEMO = "Vorraete_Demo.db3"
    const val SQLITE_FILENAME_TEST = "Vorraete_Test.db3"

    fun GetConnection(context: Context): MyDatabaseAccess
    {
        return MyDatabaseAccess(context, SQLITE_FILENAME_DEMO)
    }

    /// <summary>
    /// Datenbanken aus den Resourcen erstellen.
    /// </summary>
    fun RestoreDatabasesFromResourcesOnStartup(context: Context)
    {
        // Localized demo database with sample data.
        CreateLocalizedDatabaseFromAsset(context, SQLITE_FILENAME_DEMO)
    }

    /// <summary>
    /// Erstellt Datenbank aus den Resourcen.
    /// </summary>
    fun CreateLocalizedDatabaseFromAsset(context: Context, fileName: String)
    {
        val dbName = fileName
        val dbPath = context.getDatabasePath(dbName)

        if (dbPath.exists())
            return

        dbPath.parentFile?.mkdirs()

        context.assets.open(dbName).use { inputStream ->
            dbPath.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}