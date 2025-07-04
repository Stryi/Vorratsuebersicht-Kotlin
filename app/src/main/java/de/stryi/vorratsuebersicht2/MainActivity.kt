package de.stryi.vorratsuebersicht2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var buttonArtikel: Button
    private lateinit var buttonEanCodeScan: Button
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

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
            requestCameraPermission()
        }


    }

    private fun requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            // Permission has already been granted
            openEanCodeScanner()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted
                openEanCodeScanner()
            } else {
                // Permission denied. Handle the case appropriately.
            }
        }
    }

    private fun openEanCodeScanner() {
        val eanScanFragment = EanCodeScan.newInstance("EAN und QR-Code Scan", "TEST")
        eanScanFragment.show(supportFragmentManager, "EanCodeScan")
    }
}