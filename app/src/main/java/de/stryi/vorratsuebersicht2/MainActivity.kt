package de.stryi.vorratsuebersicht2

import Database
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    val cameraRequestCode = 101

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AndroidDatabase.restoreDatabasesFromResourcesOnStartup(this)

        Database.init(this, AndroidDatabase.SQLITE_FILENAME_DEMO)

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
        val result = Database.getArticlesByEanCode(eanCode);
        if (result.size === 0)
        {
            // Neuanlage Artikel
            return
        }

        val options = arrayOf("Lagerbestand", "Artikeldaten", "Einkaufszettel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Aktion wählen...")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> Log.d("Dialog", "Lagerbestand gewählt")
                1 -> Log.d("Dialog", "Artikeldaten gewählt")
                2 -> Log.d("Dialog", "Einkaufszettel gewählt")
            }
        }
        builder.show()

    }
}