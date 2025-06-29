package de.stryi.vorratsuebersicht2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var buttonArtikel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.copyDatabaseIfNeeded(this )

        buttonArtikel = findViewById(R.id.MainButton_Artikeldaten)
        buttonArtikel.setOnClickListener {
            val intent = Intent(this, ArticleListActivity::class.java)
            startActivity(intent)
        }

    }

    fun copyDatabaseIfNeeded(context: Context) {
        val dbName = "Vorraete_Demo.db3"
        val dbPath = context.getDatabasePath(dbName)

        if (dbPath.exists())
            return;

        dbPath.parentFile?.mkdirs()

        context.assets.open(dbName).use { inputStream ->
            dbPath.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            }
    }

}