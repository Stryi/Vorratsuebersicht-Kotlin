package de.stryi.vorratsuebersicht2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val CAMERA_REQUEST_CODE = 101 // Definiere einen Request-Code

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
                CAMERA_REQUEST_CODE)
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

        if (requestCode == CAMERA_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openEanCodeScanner()
            }
            else {
                Toast.makeText(this, "Kamera-Berechtigung nicht erteilt.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openEanCodeScanner() {
        val eanScanFragment = EanCodeScan.newInstance("EAN und QR-Code Scan", "TEST")
        eanScanFragment.show(supportFragmentManager, "EanCodeScan")
    }
}