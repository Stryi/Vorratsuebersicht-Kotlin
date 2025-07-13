package de.stryi.vorratsuebersicht2

import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.databinding.ArticleDetailsBinding

class ArticleDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ArticleDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ArticleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articleId = intent.getIntExtra("articleId", 0)
        val article = Database.getArticle(articleId)

        binding.ArticleDetailsName.setText(article?.name)
        binding.ArticleDetailsManufacturer.setText(article?.manufacturer)
        binding.ArticleDetailsSubCategory.setText(article?.subCategory)

    }
}