package de.stryi.vorratsuebersicht2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.database.Database
import de.stryi.vorratsuebersicht2.databinding.ArticleImageBinding
import de.stryi.vorratsuebersicht2.tools.Tools
import resize
import rotate
import toPngByteArray
import java.util.Locale


class ArticleImageActivity : AppCompatActivity() {

    private lateinit var binding: ArticleImageBinding

    var articleId: Int = 0
    var editMode: Boolean = false
    var isChanged: Boolean = false

    lateinit var rotatedBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ArticleImageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        this.setSupportActionBar(binding.ArticleImageAppBar)

        this.articleId = intent.getIntExtra("ArticleId", 0)
        this.editMode  = intent.getBooleanExtra("EditMode", false)
        val text = Database.getArticleName(this.articleId)

        binding.ArticleImageAppBar.setNavigationOnClickListener { finish() }
        binding.ArticleImageAppBar.setOnMenuItemClickListener { this.onOptionsItemSelected(it) }
        binding.ArticleImageImage.setOnClickListener({ this.showImageInformation() })

        this.actionBar?.title = text

        if (ArticleDetailsActivity.imageLarge != null)
        {
            this.showPictureFromBitmap()
        }
        else
        {
            this.showPictureFromDatabase(articleId)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?)
    {
        super.onCreateContextMenu(menu, v, menuInfo)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.article_image_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        if (!this.editMode)
        {
            val itemRotate = menu.findItem(R.id.ArticleImage_Menu_RotateRight)
            itemRotate.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ArticleImage_Menu_RotateRight -> {
                this.rotateImage()
                return true
            }
        }

        return false
    }

    override fun finish()
    {
        if (!this.isChanged)
        {
            super.finish()
            return
        }

        this.saveBitmap()

        super.finish()
    }

    private fun saveBitmap()
    {
        // Bild drehen
        //val rotatedBitmap = this.rotatedBitmap.rotate(90f)

        // Großes Bild als PNG-Bytearray speichern
        ArticleDetailsActivity.imageLarge = rotatedBitmap.toPngByteArray()

        // Verkleinertes Bild (Thumbnail) erstellen
        val smallBitmap = rotatedBitmap.resize(48 * 2, 85 * 2)

        // Thumbnail als PNG-Bytearray speichern
        ArticleDetailsActivity.imageSmall = smallBitmap.toPngByteArray()

        val intent = Intent()
        this.setResult(RESULT_OK, intent)

        //this.rotatedBitmap = null
        this.isChanged = false
    }

    fun showPictureFromDatabase(articleId: Int)
    {
        val article= Database.getArticleImage(articleId)

        this.showPictureFromByteArray(article?.imageLarge, article?.imageSmall)
    }

    fun showPictureFromBitmap()
    {
        this.showPictureFromByteArray(ArticleDetailsActivity.imageLarge, ArticleDetailsActivity.imageSmall)
    }

    fun showPictureFromByteArray(imageLarge: ByteArray?, imageSmall: ByteArray?)
    {
        if ((imageLarge == null) || (imageSmall == null))
        {
            binding.ArticleImageInfo.text = "Kein Bild"
            binding.ArticleImageImage.visibility = View.VISIBLE
            return
        }

        try
        {
            val largeBitmap = BitmapFactory.decodeByteArray(imageLarge, 0, imageLarge.size)
            this.rotatedBitmap = largeBitmap
            binding.ArticleImageImage.setImageBitmap(largeBitmap)

            var message = String.format(
                Locale.getDefault(),
                "Bild (BxH): %,d x %,d (Größe: %s, Komprimiert: %s)%n",
                largeBitmap.width,
                largeBitmap.height,
                Tools.toFuzzyByteString(largeBitmap.byteCount),
                Tools.toFuzzyByteString(imageLarge.size))

            val smallBitmap = BitmapFactory.decodeByteArray(imageSmall, 0, imageSmall.size)

            message += String.format(
                Locale.getDefault(),
                "Thn. (BxH): %,d x %,d (Größe: %s, Komprimiert: %s)%n",
                smallBitmap.width,
                smallBitmap.height,
                Tools.toFuzzyByteString(smallBitmap.byteCount),
                Tools.toFuzzyByteString(imageSmall.size))

            binding.ArticleImageInfo.text = message
        }
        catch (e: Exception)
        {
            binding.ArticleImageInfo.text = e.message
            binding.ArticleImageImage.visibility = View.GONE
        }
    }

    private fun rotateImage()
    {
        val matrix = Matrix()
        matrix.postRotate(90f)

        val rotated = Bitmap.createBitmap(
            rotatedBitmap,
            0, 0,
            rotatedBitmap.width,
            rotatedBitmap.height,
            matrix,
            true
        )

        binding.ArticleImageImage.setImageBitmap(rotated)
        rotatedBitmap = rotated
        this.isChanged = true
    }

    private fun showImageInformation()
    {
        if (binding.ArticleImageInfo.visibility != View.VISIBLE)
        {
            binding.ArticleImageInfo.visibility = View.VISIBLE
        }
        else
        {
            binding.ArticleImageInfo.visibility = View.GONE
        }
    }
}