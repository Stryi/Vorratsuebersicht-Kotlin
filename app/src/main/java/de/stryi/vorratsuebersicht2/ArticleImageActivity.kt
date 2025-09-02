package de.stryi.vorratsuebersicht2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.stryi.vorratsuebersicht2.database.Database

class ArticleImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.article_image)
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        */

        val articleId = intent.getIntExtra("ArticleId", 0)

        showPictureFromDatabase(articleId)
    }

    fun showPictureFromDatabase(articleId: Int)
    {
        val byteArray = Database.getArticleImage(articleId)
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val image: ImageView = findViewById(R.id.ArticleImage_Image)
        image.setImageBitmap(bitmap)
    }
}