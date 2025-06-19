package de.stryi.vorratsuebersicht2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button = findViewById(R.id.MainButton_Artikeldaten)
        button.setOnClickListener {
            val intent = Intent(this, ArticleListActivity::class.java)
            startActivity(intent)
        }

    }
}