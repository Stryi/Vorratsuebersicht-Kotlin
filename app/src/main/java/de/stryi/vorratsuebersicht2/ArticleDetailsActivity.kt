package de.stryi.vorratsuebersicht2

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

        binding.ArticleDetailsArticleId.text = article.articleId.toString()
        binding.ArticleDetailsName.setText(article.name)
        binding.ArticleDetailsManufacturer.setText(article.manufacturer)
        binding.ArticleDetailsSubCategory.setText(article.subCategory)
        binding.ArticleDetailsSupermarket.setText(article.supermarket)
        binding.ArticleDetailsStorage.setText(article.storageName)

        // Hersteller Eingabe
        val  manufacturers = mutableListOf<String?>()
        manufacturers.addAll(Database.getManufacturerNames())
        val manufacturerAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, manufacturers)
        binding.ArticleDetailsManufacturer.setAdapter(manufacturerAdapter)
        binding.ArticleDetailsManufacturer.threshold = 1

        binding.ArticleDetailsSelectManufacturer.setOnClickListener { this.selectManufacturer() }

        // Kategorie Auswahl
        // TODO:

        // Unterkategorie Eingabe
        val subCategories = Database.getSubcategoriesOf()
        val subCategoryAdapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,subCategories)
        binding.ArticleDetailsSubCategory.setAdapter(subCategoryAdapter)
        binding.ArticleDetailsSubCategory.threshold = 1

        binding.ArticleDetailsSelectSubCategory.setOnClickListener { this.selectSubCategory() }

        // Einkaufsmarkt Eingabe
        val supermarkets = Database.getSupermarketNames()
        val supermarketAdapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,supermarkets)
        binding.ArticleDetailsSupermarket.setAdapter(supermarketAdapter)
        binding.ArticleDetailsSupermarket.threshold = 1

        binding.ArticleDetailsSelectSupermarket.setOnClickListener { this.selectSupermarket() }


        // Lagerort Eingabe
        val storages = Database.getStorageNames()
        val storageAdapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,storages)
        binding.ArticleDetailsStorage.setAdapter(storageAdapter)
        binding.ArticleDetailsStorage.threshold = 1

        binding.ArticleDetailsSelectStorage.setOnClickListener { this.selectStorage() }
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

    private fun selectManufacturer()
    {
        val  manufacturers = mutableListOf<String?>()
        manufacturers.addAll(Database.getManufacturerNames())

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.ArticleDetails_Manufacturer)
        builder.setItems(manufacturers.toTypedArray()) { _, which ->
            binding.ArticleDetailsManufacturer.setText(manufacturers[which])
        }
        builder.show()
    }

    private fun selectSubCategory()
    {
        //val category = binding.ArticleDetailsCategory.selectedItem

        val subCategories    = Database.getSubcategoriesOf(binding.ArticleDetailsCategory.selectedItem.toString())

        if  (subCategories.count() > 0)
        {
            // Empty entry as delimitation and for deleting the subcategory.
            subCategories.add("")
        }

        // All other categories.
        for (subCategory in Database.getSubcategoriesOf())
        {
            if (!subCategories.contains(subCategory))
            {
                subCategories.add(subCategory)
            }
        }

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.ArticleDetails_SubCategory)
        builder.setItems(subCategories.toTypedArray()) { _, which ->
            binding.ArticleDetailsSubCategory.setText(subCategories[which])
        }
        builder.show()
    }

    private fun selectSupermarket() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.ArticleDetails_SupermarketLabel)
        builder.setItems(Database.getSupermarketNames().toTypedArray()) { _, which ->
            binding.ArticleDetailsSupermarket.setText(Database.getSupermarketNames()[which])
        }
        builder.show()
    }

    private fun selectStorage() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.ArticleDetails_StorageLabel)
        builder.setItems(Database.getStorageNames().toTypedArray()) { _, which ->
            binding.ArticleDetailsStorage.setText(Database.getStorageNames()[which])
        }
        builder.show()
    }


    fun saveArticle() : Boolean {
        try {
            val article = Article()
            article.articleId = binding.ArticleDetailsArticleId.text.toString().toInt()
            article.name = binding.ArticleDetailsName.text.toString()
            article.manufacturer = binding.ArticleDetailsManufacturer.text.toString()
            article.subCategory = binding.ArticleDetailsSubCategory.text.toString()
            article.supermarket = binding.ArticleDetailsSupermarket.text.toString()
            article.storageName = binding.ArticleDetailsStorage.text.toString()

            Database.updateArticle(article)
        }
        catch (ex: Exception)
        {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            return false
        }

        isChanged = true

        return true
    }
}
