package de.stryi.vorratsuebersicht2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.database.Article
import de.stryi.vorratsuebersicht2.database.Database
import de.stryi.vorratsuebersicht2.databinding.ArticleListBinding
import java.util.Random

class ArticleListActivity : AppCompatActivity() {

    //var liste = mutableListOf<Article>()

    private lateinit var binding: ArticleListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ArticleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setSupportActionBar(binding.ArticleListAppBar)

        binding.ArticleListAppBar.setNavigationOnClickListener {finish() }

        binding.ArticleList.setOnItemClickListener(this::onOpenArticleDetails)

        /*
        val adapter = object : ArrayAdapter<Article>(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            articles
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text1 = view.findViewById<TextView>(android.R.id.text1)
                val text2 = view.findViewById<TextView>(android.R.id.text2)

                val article = getItem(position)
                if (article != null) {
                    text1.text = article.name
                    text2.text = "${article.eanCode}\r\n${article.category}\r\n${article.manufacturer}"
                }
                return view
            }
        }
        listView.adapter = adapter
        */

        showArticleList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.article_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ArticleList_Menu_Add -> {
                // Handle settings
                Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.ArticleList_Menu_Filter -> {
                // Handle settings
                Toast.makeText(this, "Filter", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.ArticleList_Menu_Share -> {
                // Handle settings
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return false
        }
    }


    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.article_list_menu, menu)
    }

    @SuppressLint("StringFormatInvalid")
    fun showArticleList(text: String? = null)
    {

        val articleList = Database.getArticleList()

        /*
        // Liste merken, damit sie später für ShareList() verwendet werden kann.
        this.liste.clear()
        for (article in articles)
        {
            liste.add(article)
        }
        */

        val listView = findViewById<ListView>(R.id.ArticleList)

        val adapter = object : ArrayAdapter<Article>(this, R.layout.article_list_view, articleList) {
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.article_list_view, parent, false)
                val item = getItem(position)

                view.tag = item?.articleId;

                view.findViewById<TextView>(R.id.ArticleListView_Heading).text = item?.name
                view.findViewById<TextView>(R.id.ArticleListView_SubHeading).text = item?.subHeading
                view.findViewById<TextView>(R.id.ArticleListView_Notes).text = item?.notesText
                view.findViewById<TextView>(R.id.ArticleListView_Notes).visibility = if (item?.notesText.isNullOrEmpty()) View.GONE else View.VISIBLE

                view.findViewById<ImageView>(R.id.ArticleListView_OnShoppingList).visibility   = View.VISIBLE
                view.findViewById<TextView> (R.id.ArticleListView_ShoppingQuantity).visibility = View.VISIBLE
                view.findViewById<TextView> (R.id.ArticleListView_ShoppingQuantity).text       = Random().nextInt(20).plus(1).toString()
                view.findViewById<ImageView>(R.id.ArticleListView_IsInStorage).visibility      = View.VISIBLE
                view.findViewById<TextView> (R.id.ArticleListView_StorageQuantity).visibility  = View.VISIBLE
                view.findViewById<TextView> (R.id.ArticleListView_StorageQuantity).text        = Random().nextInt(200).plus(1).toString()


                val image = view.findViewById<ImageView>(R.id.ArticleListView_Image)

                val byteArray = Database.getArticleImage(item?.articleId, false)

                if (byteArray.isEmpty())
                {
                    image.setImageResource(R.drawable.photo_camera_24px)
                    image.alpha = 0.2.toFloat()
                }
                else
                {
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    image.setImageBitmap(bitmap)
                }

                return view
            }
        }
        listView.adapter = adapter

        var status = ""

        if (articleList.count() == 1)
            status = this.resources.getString(R.string.ArticleListSummary_Position)
        else
            status = this.resources.getString(R.string.ArticleListSummary_Positions)

        binding.ArticleListFooter.text = String.format(status, articleList.count())
    }

    fun onOpenArticleDetails(parent: AdapterView<*>, view: View?, position: Int, id: Long)
    {
        val articleId = view?.tag as Int
        val intent = Intent(this, ArticleDetailsActivity::class.java)
        intent.putExtra("articleId", articleId)
        startActivity(intent)
    }
}