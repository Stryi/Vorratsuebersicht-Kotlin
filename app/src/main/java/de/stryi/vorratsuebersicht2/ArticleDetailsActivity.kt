package de.stryi.vorratsuebersicht2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import de.stryi.vorratsuebersicht2.databinding.ArticleDetailsBinding

class ArticleDetailsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ArticleDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ArticleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}