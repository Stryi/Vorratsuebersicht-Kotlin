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

            val eanScanFragment = EanCodeScan.newInstance("Test1", "Test2")

            // 2. FragmentTransaction starten, um das Fragment anzuzeigen
            supportFragmentManager.beginTransaction()
                .replace(R.id.FragmentLayout, eanScanFragment) // WICHTIG: Ersetzen Sie R.id.fragment_container
                // mit der tatsächlichen ID des FrameLayouts
                // in Ihrem activity_main.xml, das als
                // Container für Fragmente dienen soll.
                .addToBackStack(null) // Optional: Ermöglicht das Zurücknavigieren zum vorherigen Zustand
                .commit()
        }


    }
}