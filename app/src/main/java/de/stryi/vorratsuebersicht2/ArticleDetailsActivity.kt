package de.stryi.vorratsuebersicht2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.database.Article
import de.stryi.vorratsuebersicht2.database.Database
import de.stryi.vorratsuebersicht2.databinding.ArticleDetailsBinding


class ArticleDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ArticleDetailsBinding

    //private lateinit val articleImage: ByteArray?

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

        var articleImage = Database.getArticleImage(articleId, false)

        if (!articleImage.isEmpty())
        {
            val smallBitmap: Bitmap? = BitmapFactory.decodeByteArray(
                articleImage,
                0,
                articleImage.size)

            binding.ArticleDetailsImage.setImageBitmap(smallBitmap)
            binding.ArticleDetailsImage2.visibility = View.GONE
        }

        binding.ArticleDetailsArticleId.text = "ArticleId: ${article.articleId}"
        binding.ArticleDetailsName.setText(article.name)
        binding.ArticleDetailsManufacturer.setText(article.manufacturer)
        binding.ArticleDetailsSubCategory.setText(article.subCategory)
        binding.ArticleDetailsSupermarket.setText(article.supermarket)
        binding.ArticleDetailsStorage.setText(article.storageName)
        binding.ArticleDetailsDurableInfinity.isChecked = article.durableInfinity
        binding.ArticleDetailsWarnInDays.setText(article.warnInDays.toString())
        binding.ArticleDetailsPrice.setText(article.price.toString())
        binding.ArticleDetailsSize.setText(article.size.toString())
        binding.ArticleDetailsUnit.setText(article.unit)
        binding.ArticleDetailsCalorie.setText(article.calorie.toString())
        binding.ArticleDetailsMinQuantity.setText(article.minQuantity.toString())
        binding.ArticleDetailsPrefQuantity.setText(article.prefQuantity.toString())
        binding.ArticleDetailsEANCode.setText(article.eanCode)
        binding.ArticleDetailsNotes.setText(article.notes)


        // Hersteller Eingabe
        val manufacturers = Database.getManufacturerNames()
        val manufactureresAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, manufacturers)
        binding.ArticleDetailsManufacturer.setAdapter(manufactureresAdapter)
        binding.ArticleDetailsManufacturer.threshold = 1

        binding.ArticleDetailsSelectManufacturer.setOnClickListener { this.selectManufacturer() }


        // Fest definierte Kategorien
        val defaultCategories = R.array.ArticleCatagories

        val categoryAdapter = ArrayAdapter.createFromResource(this, defaultCategories, android.R.layout.simple_dropdown_item_1line)
        binding.ArticleDetailsCategory.adapter = categoryAdapter

        // Unterkategorie Eingabe
        val subCategories = Database.getSubcategoriesOf()
        val subCategoriesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subCategories)
        binding.ArticleDetailsSubCategory.setAdapter(subCategoriesAdapter)
        binding.ArticleDetailsSubCategory.threshold = 1

        binding.ArticleDetailsSelectSubCategory.setOnClickListener { this.selectSubCategory() }

        // Einkaufsmarkt Eingabe
        val supermarkets = Database.getSupermarketNames()
        val supermarketsAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, supermarkets)
        binding.ArticleDetailsSupermarket.setAdapter(supermarketsAdapter)
        binding.ArticleDetailsSupermarket.threshold = 1

        binding.ArticleDetailsSelectSupermarket.setOnClickListener { this.selectSupermarket() }

        // Lagerort Eingabe
        val storages = Database.getStorageNames()
        val storagesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, storages)
        binding.ArticleDetailsStorage.setAdapter(storagesAdapter)
        binding.ArticleDetailsStorage.threshold = 1

        binding.ArticleDetailsSelectStorage.setOnClickListener { this.selectStorage() }
    }


    private fun selectManufacturer()
    {
        val manufacturers = Database.getManufacturerNames()

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.ArticleDetails_Manufacturer)
        builder.setItems( manufacturers.toTypedArray()) { _, which ->
            binding.ArticleDetailsManufacturer.setText( manufacturers[which])
        }
        builder.show()
    }

    private fun selectSubCategory()
    {
        val category = binding.ArticleDetailsCategory.selectedItem.toString()

        val subCategories = Database.getSubcategoriesOf(category)

        if (!subCategories.isEmpty())
        {
            subCategories.add("")
        }

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

    private fun selectSupermarket()
    {
        val supermarkets = Database.getSupermarketNames()
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.ArticleDetails_SupermarketLabel)
        builder.setItems(supermarkets.toTypedArray()) { _, which ->
            binding.ArticleDetailsSupermarket.setText(supermarkets[which])
        }
        builder.show()
    }

    private fun selectStorage()
    {
        val storageNames = Database.getStorageNames()

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.ArticleDetails_StorageLabel)
        builder.setItems(storageNames.toTypedArray()) { _, which ->
            binding.ArticleDetailsStorage.setText(storageNames[which])
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
