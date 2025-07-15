package de.stryi.vorratsuebersicht2

import de.stryi.vorratsuebersicht2.database.Database
import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import de.stryi.vorratsuebersicht2.database.AndroidDatabase
import de.stryi.vorratsuebersicht2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    val cameraRequestCode = 101

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.MainAppBar)

        AndroidDatabase.restoreDatabasesFromResourcesOnStartup(this)

        Database.init(this, AndroidDatabase.SQLITE_FILENAME_PROD)

        setSupportActionBar(binding.MainAppBar)

        binding.MainAppBar.setNavigationOnClickListener {
            // Zurück, Menü etc.
        }

        binding.MainAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Main_Menu_SelectDatabase -> {
                    // Handle settings
                    true
                }
                R.id.Main_Menu_Options -> {
                    // Handle settings
                    true
                }
                else -> false
            }
        };

        val buttonCategory = findViewById<Button>(R.id.MainButton_Kategorie)
        buttonCategory.setOnClickListener {
        }

        val buttonArticle = findViewById<Button>(R.id.MainButton_Artikeldaten)
        buttonArticle.setOnClickListener {
            val intent = Intent(this, ArticleListActivity::class.java)
            startActivity(intent)
        }

        // Barcode scannen
        val buttonBarcode = findViewById<Button>(R.id.MainButton_Barcode)
        buttonBarcode.setOnClickListener {
            PermissionHelper().requestPermission(
                this,
                Manifest.permission.CAMERA,
                cameraRequestCode)
            {
                openEanCodeScanner()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == cameraRequestCode){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openEanCodeScanner()
            }
            else {
                Toast.makeText(this, "Kamera-Berechtigung nicht erteilt.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openEanCodeScanner() {
        val eanScanFragment = EanCodeScan()
        eanScanFragment.onResult = { eanCode ->
            Toast.makeText(this, eanCode, Toast.LENGTH_SHORT).show()

            searchEANCode(eanCode)
        }
        eanScanFragment.show(supportFragmentManager, "EanCodeScan")
    }

    fun searchEANCode(eanCode: String)
    {
        val result = Database.getArticlesByEanCode(eanCode)
        if (result.isEmpty())
        {
            // Neuanlage Artikel
            val articleDetails = Intent(this, ArticleDetailsActivity::class.java)
            articleDetails.putExtra("EANCode", eanCode)
            startActivity(articleDetails)
            return
        }

        val options = arrayOf("Lagerbestand", "Artikeldaten", "Einkaufszettel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Aktion wählen...")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> Log.d("Dialog", "Lagerbestand gewählt")
                1 -> {
                    // Artikelstamm bearbeiten
                    val articleDetails = Intent(this, ArticleDetailsActivity::class.java)
                    articleDetails.putExtra("EANCode", eanCode)
                    startActivity(articleDetails)
                }
                2 -> Log.d("Dialog", "Einkaufszettel gewählt")
            }
        }
        builder.show()

    }
}