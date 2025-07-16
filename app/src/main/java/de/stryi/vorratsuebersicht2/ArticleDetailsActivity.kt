package de.stryi.vorratsuebersicht2

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.database.Article
import de.stryi.vorratsuebersicht2.database.Database
import de.stryi.vorratsuebersicht2.databinding.ArticleDetailsBinding

class ArticleDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ArticleDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ArticleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setSupportActionBar(binding.ArticleDetailsAppBar)

        binding.ArticleDetailsAppBar.setNavigationOnClickListener { finish() }

        val articleId = intent.getIntExtra("articleId", 0)
        var article = Database.getArticle(articleId)
        if (article == null)
        {
            article = Article()
        }

        binding.ArticleDetailsName.setText(article.name)
        binding.ArticleDetailsManufacturer.setText(article.manufacturer)
        binding.ArticleDetailsSubCategory.setText(article.subCategory)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.article_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ArticleDetailsMenu_Save -> {
                // Handle settings
                Toast.makeText(this, "Sav", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.ArticleDetailsMenu_Cancel -> {
                // Handle settings
                Toast.makeText(this, "Cance", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return false
        }
    }

}