package de.stryi.vorratsuebersicht2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.database.Article
import de.stryi.vorratsuebersicht2.database.ArticleImage
import de.stryi.vorratsuebersicht2.database.Database
import de.stryi.vorratsuebersicht2.databinding.ArticleDetailsBinding
import java.math.BigDecimal
import java.util.Date


class ArticleDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ArticleDetailsBinding

    companion object {
        var imageLarge: Bitmap? = null
        var imageSmall: Bitmap? = null
    }

    private lateinit var article: Article
    private var articleImage: ArticleImage? = null
    private var articleId: Int = 0
    private var isChanged: Boolean = false
    private var noStorageQuantity: Boolean = false
    private var noDeleteArticle: Boolean = false

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview())
    { bitmap: Bitmap? ->
        if (bitmap == null)
            return@registerForActivityResult

        this.resizeBitmap(bitmap)
    }

    // Launcher für das Bild-Auswahl-Intent
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri == null)
            return@registerForActivityResult

        this.loadAndResizeBitmap(uri)
    }

    private var isPhotoSelected: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ArticleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setSupportActionBar(binding.ArticleDetailsAppBar)

        binding.ArticleDetailsAppBar.setNavigationOnClickListener { finish() }

        this.articleId = intent.getIntExtra("ArticleId", 0)
        var article = Database.getArticle(this.articleId)
        if (article == null)
        {
            article = Article()
            this.isPhotoSelected = false
        }
        this.article = article

        this.articleImage = Database.getArticleImage(articleId, false)

        if (this.articleImage != null)
        {
            val smallBitmap: Bitmap? = BitmapFactory.decodeByteArray(
                articleImage!!.imageSmall,
                0,
                articleImage!!.imageSmall!!.size)

            binding.ArticleDetailsImage.setImageBitmap(smallBitmap)
            binding.ArticleDetailsImage2.visibility = View.GONE
        }

        binding.ArticleDetailsArticleId.text = "ArticleId: ${this.article.articleId}"
        binding.ArticleDetailsName.setText(this.article.name)
        binding.ArticleDetailsManufacturer.setText(this.article.manufacturer)
        binding.ArticleDetailsSubCategory.setText(this.article.subCategory)
        binding.ArticleDetailsSupermarket.setText(this.article.supermarket)
        binding.ArticleDetailsStorage.setText(this.article.storageName)
        binding.ArticleDetailsDurableInfinity.isChecked = this.article.durableInfinity
        binding.ArticleDetailsWarnInDays.setText(this.article.warnInDays.toString())
        binding.ArticleDetailsPrice.setText(this.article.price.toString())
        binding.ArticleDetailsSize.setText(this.article.size.toString())
        binding.ArticleDetailsUnit.setText(this.article.unit)
        binding.ArticleDetailsCalorie.setText(this.article.calorie.toString())
        binding.ArticleDetailsMinQuantity.setText(this.article.minQuantity.toString())
        binding.ArticleDetailsPrefQuantity.setText(this.article.prefQuantity.toString())
        binding.ArticleDetailsEANCode.setText(this.article.eanCode)
        binding.ArticleDetailsNotes.setText(this.article.notes)

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

        binding.ArticleDetailsImage.setOnClickListener { this.takeOrShowPhoto() }
        binding.ArticleDetailsImage2.setOnClickListener { this.selectAPicture() }
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

    private fun takeOrShowPhoto()
    {
        if (!this.isPhotoSelected) {
            this.takeAPhoto()
            return
        }

        val articleImage = Intent(this, ArticleImageActivity::class.java)
        articleImage.putExtra("ArticleId", this.articleId)
        articleImage.putExtra("EditMode", true)
        this.startActivity(articleImage)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.article_details_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        val itemDelete = menu.findItem(R.id.ArticleDetailsMenu_Delete)
        itemDelete.isVisible = this.article.articleId > 0

        val itemShowPicture = menu.findItem(R.id.ArticleDetailsMenu_ShowPicture)
        itemShowPicture.isEnabled = this.isPhotoSelected

        val itemRemovePicture = menu.findItem(R.id.ArticleDetailsMenu_RemovePicture)
        itemRemovePicture.isEnabled = this.isPhotoSelected

        if (MainActivity.IsGooglePlayPreLaunchTestMode)
        {
            val itemEanScan = menu.findItem(R.id.ArticleDetailsMenu_ScanEAN)
            itemEanScan.isEnabled = false
        }

        val eanCode = binding.ArticleDetailsEANCode.text.toString()

        val itemInternetDB = menu.findItem(R.id.ArticleDetailsMenu_InternetDB)
        itemInternetDB.isEnabled = eanCode.isNotEmpty()

        menu.findItem(R.id.ArticleDetailsMenu_ToStorageQuantity).isVisible = !this.noStorageQuantity
        menu.findItem(R.id.ArticleDetailsMenu_Delete).isVisible = !this.noDeleteArticle

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ArticleDetailsMenu_Delete -> {
                this.deleteArticle()
                return  true
            }

            R.id.ArticleDetailsMenu_Save -> {
                if (!this.saveArticle())
                {
                    return false
                }
                this.finish()
                return true
            }

            R.id.ArticleDetailsMenu_Cancel -> {
                this.moveTaskToBack(false)
                this.finish()
                return true
            }

            R.id.ArticleDetailsMenu_MakeAPhoto -> {
                this.takeAPhoto()
                return true
            }

            R.id.ArticleDetailsMenu_SelectAPicture -> {
                this.selectAPicture()
                return true
            }

            R.id.ArticleDetailsMenu_ShowPicture -> {
                if (this.isPhotoSelected)
                {
                    val articleImage = Intent(this, ArticleImageActivity::class.java)
                    articleImage.putExtra("ArticleId", this.articleId)
                    articleImage.putExtra("EditMode", true)
                    this.startActivity(articleImage)
                }
                return true
            }

            R.id.ArticleDetailsMenu_RemovePicture -> {
                if (this.isPhotoSelected)
                {
                    // Erstelltes oder ausgewähltes Bild entfernen
                    ArticleDetailsActivity.imageLarge = null
                    ArticleDetailsActivity.imageSmall = null

                    //this.articleImage.ImageLarge = null    // Änderungen verwerfen
                    //this.articleImage.ImageSmall = null    // Gespeichertes Bild auch löschen

                    binding.ArticleDetailsImage.setImageResource(R.drawable.photo_camera_24px)
                    binding.ArticleDetailsImage2.setImageResource(R.drawable.photo_24px)
                    binding.ArticleDetailsImage2.visibility = View.VISIBLE

                    this.isPhotoSelected = false
                    this.isChanged = true
                }
            }
        }

        return false
    }

    private fun searchEanCodeOnInternetDb() {
        Toast.makeText(this, "TODO: Suche Artikel auf Open Food Facts", Toast.LENGTH_LONG).show()
    }

    private fun saveAndAddToShoppingList() {
        Toast.makeText(this, "TODO: Artikel zum Einkaufswagen hinzufügen", Toast.LENGTH_LONG).show()
    }

    private fun saveAndGoToStorageItem() {
        Toast.makeText(this, "TODO: Artikel zum Lagerbestand hinzufügen", Toast.LENGTH_LONG).show()
    }

    private fun goToStorageItem(articleId: Int) {
        Toast.makeText(this, "TODO: Artikel zum Lagerbestand hinzufügen", Toast.LENGTH_LONG).show()
    }

    private fun searchEANCode(eanCode: String) {
        Toast.makeText(this, "TODO: Suche Artikel in der Artikel Tabelle", Toast.LENGTH_LONG).show()
    }

    override fun finish() {
        if (isChanged) {
            val returnIntent = Intent()
            returnIntent.putExtra("result", "Mein Wert")
            setResult(RESULT_OK, returnIntent)
        }

        //this.article = null
        ArticleDetailsActivity.imageLarge = null
        ArticleDetailsActivity.imageSmall = null
        // TODO InternetDatabaseSearchActivity.picture = null

        super.finish()
    }

    private fun selectAPicture() {
        pickImageLauncher.launch("image/*")
    }

    private fun takeAPhoto() {
        if (MainActivity.IsGooglePlayPreLaunchTestMode)
        {
            return
        }

        takePicturePreview.launch(null)
    }

    private fun saveArticle() : Boolean {
        try {
            val article = Article()
            article.articleId = binding.ArticleDetailsArticleId.text.toString().toInt()
            article.name = binding.ArticleDetailsName.text.toString()
            article.manufacturer = binding.ArticleDetailsManufacturer.text.toString()
            article.subCategory = binding.ArticleDetailsSubCategory.text.toString()
            article.supermarket = binding.ArticleDetailsSupermarket.text.toString()
            article.storageName = binding.ArticleDetailsStorage.text.toString()

            Database.updateArticle(article)

            /*
            if (this.articleImage!!.imageLarge != null)   // Ein neues Bild wurde ausgewählt oder vorhandenes geändert.
            {
                if (this.articleImage!!.imageId > 0)
                {
                    Database.Update(this.articleImage)
                }
                else
                {
                    this.articleImage!!.articleId = this.articleId
                    this.articleImage!!.type = 0
                    this.articleImage!!.createdAt = Date()
                    Database.Insert(this.articleImage)
                }
            }

            if ((this.articleImage!!.imageSmall == null) && (this.articleImage!!.imageId > 0))  // Vorhandenes Bild gelöscht?
            {
                Database.Delete(this.articleImage);
            }
            */
        }
        catch (ex: Exception)
        {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            return false
        }

        isChanged = true

        return true
    }

    private fun deleteArticle() {
        Toast.makeText(this, "TODO: Artikel löschen", Toast.LENGTH_LONG).show()
    }

    private fun addToShoppingListManually() {
        Toast.makeText(this, "TODO: Artikel zum Einkaufswagen hinzufügen", Toast.LENGTH_LONG).show()
    }

    private fun showPictureAndDetails() {
        Toast.makeText(this, "TODO: Bild anzeigen", Toast.LENGTH_LONG).show()
    }

    private fun ShowStoreQuantityInfo() {
        Toast.makeText(this, "TODO: Lagerbestand anzeigen", Toast.LENGTH_LONG).show()
    }

    private fun resizeBitmap(newBitmap: Bitmap)
    {
        var widthLarge = 854
        var heightLarge = 854

        var largeBitmap = newBitmap

        val compress = true // TODO: Settings.GetBoolean("CompressPictures", true);
        if (compress)
        {
            val compressMode = 4 // TODO: Settings.GetInt("CompressPicturesMode", 1);
            if (compressMode == 2)
            {
                widthLarge  = 1_024
                heightLarge = 1_024
            }

            if (compressMode == 3)
            {
                widthLarge  = 1_280
                heightLarge = 1_280
            }

            if (compressMode == 4)
            {
                widthLarge  = 1_536
                heightLarge = 1_536
            }

            widthLarge = Math.min (newBitmap.width,  widthLarge)
            heightLarge = Math.min(newBitmap.height, heightLarge)

            largeBitmap = Bitmap.createScaledBitmap(newBitmap, widthLarge, heightLarge, true)
        }

        ArticleDetailsActivity.imageLarge = largeBitmap

        // --------------------------------------------------------------------------------
        // Miniaturansicht erstellen
        // --------------------------------------------------------------------------------

        var smallBitmap = Bitmap.createScaledBitmap(newBitmap, 48*2, 85*2, true)
        ArticleDetailsActivity.imageSmall = smallBitmap

        binding.ArticleDetailsImage.setImageBitmap(largeBitmap)
        binding.ArticleDetailsImage2.visibility = View.GONE

        isPhotoSelected = true
        isChanged = true

        invalidateOptionsMenu() // To update the menu items
    }

    private fun loadAndResizeBitmap(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        this.resizeBitmap(bitmap)
    }

    private fun getIntegerFromEditText(resourceId: Int): Int? {
        Toast.makeText(this, "TODO: Eingabe aus EditText lesen", Toast.LENGTH_LONG).show()

        return null
    }

    private fun getDecimalFromEditText(resourceId: Int): BigDecimal? {
        Toast.makeText(this, "TODO: Eingabe aus EditText lesen", Toast.LENGTH_LONG).show()
        return null
    }

    private fun createProgressBar() {
        Toast.makeText(this, "TODO: Fortschrittsbalken anzeigen", Toast.LENGTH_LONG).show()
    }

    private fun hideProgressBar() {
        Toast.makeText(this, "TODO: Fortschrittsbalken ausblenden", Toast.LENGTH_LONG).show()
    }
}
