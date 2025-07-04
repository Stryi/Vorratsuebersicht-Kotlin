package de.stryi.vorratsuebersicht2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var buttonArtikel: Button
    private lateinit var buttonEanCodeScan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AndroidDatabase.restoreDatabasesFromResourcesOnStartup(this)

        Database.init(this, AndroidDatabase.SQLITE_FILENAME_DEMO)

        buttonArtikel = findViewById(R.id.MainButton_Artikeldaten)
        buttonArtikel.setOnClickListener {
            val intent = Intent(this, ArticleListActivity::class.java)
            startActivity(intent)
        }

        buttonEanCodeScan = findViewById(R.id.MainButton_Barcode)
        buttonEanCodeScan.setOnClickListener {


            val eanScanFragment = EanCodeScan.newInstance("TEST", "Test2")

            // Fragment modal anzeigen
            eanScanFragment.show(supportFragmentManager, "EanCodeScan")

            /*
            supportFragmentManager.beginTransaction()
                .replace(R.id.FragmentLayout, eanScanFragment)
                .addToBackStack(null)
                .commit()
             */
        }


    }
}