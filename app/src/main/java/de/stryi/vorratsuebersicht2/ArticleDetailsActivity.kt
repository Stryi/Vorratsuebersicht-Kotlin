package de.stryi.vorratsuebersicht2

import android.R.string
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.database.Article
import de.stryi.vorratsuebersicht2.database.Database
import de.stryi.vorratsuebersicht2.databinding.ArticleDetailsBinding


class ArticleDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ArticleDetailsBinding

    private var isChanged: Boolean = false

    val  manufacturers = mutableListOf<String?>()

    //private var manufacturers =

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

        binding.ArticleDetailsArticleId.setText(article.articleId.toString())
        binding.ArticleDetailsName.setText(article.name)
        binding.ArticleDetailsManufacturer.setText(article.manufacturer)
        binding.ArticleDetailsSubCategory.setText(article.subCategory)

        // Hersteller Eingabe
        this.manufacturers.addAll(Database.getManufacturerNames())
        binding.ArticleDetailsManufacturer.threshold = 1
        val manufacturerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            manufacturers
        )
        binding.ArticleDetailsManufacturer.setAdapter(manufacturerAdapter)

        binding.ArticleDetailsSelectManufacturer.setOnClickListener {
            this.selectManufacturer()
        }

        // Kategorie



    }

    private fun selectManufacturer()
    {
        var builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.ArticleDetails_Manufacturer)
        builder.setItems(this.manufacturers.toTypedArray()) { _, which ->
            binding.ArticleDetailsManufacturer.setText(this.manufacturers[which])
        }
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.article_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ArticleDetailsMenu_Save -> {
                if (!this.saveArticle())
                {
                    return false
                }
                this.finish()
                return true
            }
            R.id.ArticleDetailsMenu_Cancel -> {
                this.finish()
                return true
            }
            else -> return false
        }
    }

    override fun finish() {
        if (isChanged) {
            val returnIntent = Intent()
            returnIntent.putExtra("result", "Mein Wert")
            setResult(RESULT_OK, returnIntent)
        }
        super.finish()
    }

    fun saveArticle() : Boolean {
        try {
            val article = Article()
            article.articleId = binding.ArticleDetailsArticleId.text.toString().toInt()
            article.name = binding.ArticleDetailsName.text.toString()
            article.manufacturer = binding.ArticleDetailsManufacturer.text.toString()
            article.subCategory = binding.ArticleDetailsSubCategory.text.toString()

            Database.updateArticle(article)
        }
        catch (ex: Exception)
        {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            return false
        }

        isChanged = true;

        return true
    }
}
