package de.stryi.vorratsuebersicht2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.SearchEvent
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.stryi.vorratsuebersicht2.database.Database
import de.stryi.vorratsuebersicht2.databinding.ArticleListBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArticleListActivity : AppCompatActivity() {

    private var listViewState: Parcelable? = null
    private lateinit var binding: ArticleListBinding

    private val detailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //val data = result.data
                //val updatedArticleId = data?.getIntExtra("articleId", -1)

                showArticleList()

                binding.ArticleList.layoutManager?.onRestoreInstanceState(listViewState)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ArticleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setSupportActionBar(binding.ArticleListAppBar)

        binding.ArticleListAppBar.setNavigationOnClickListener { finish() }

        binding.ArticleList.isClickable = true

        showArticleList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Zeigt die Icons im Menü an, bringt aber Fehler beim Übersetzen
        // https://medium.com/@ssuubbiirr/optionsmenuitem-with-icon-and-title-in-android-20cdbad87a3f
        //if (menu is MenuBuilder) (menu as MenuBuilder).setOptionalIconsVisible(true)
        menuInflater.inflate(R.menu.article_list_menu, menu)

        val searchItem = menu.findItem(R.id.ArticleList_Menu_Search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Artikel suchen..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Optional: Suche abschließen
                //showArticleList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //filterArticles(newText.orEmpty())
                showArticleList(newText)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ArticleList_Menu_Add -> {
                onCreateArticle();
                return true
            }
            R.id.ArticleList_Menu_Filter -> {
                // Handle settings
                Toast.makeText(this, "Filter", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.ArticleList_Menu_Share -> {
                shareList()
                return true
            }
            else -> return false
        }
    }


    fun showArticleList(text: String? = null)
    {
        val articleList = Database.getArticleList(text)

        val adapter = ArticleListViewAdapter(articleList, this::onOpenArticleDetails)

        val recyclerView = findViewById<RecyclerView>(R.id.ArticleList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        var status: String

        if (articleList.count() == 1)
            status = this.resources.getString(R.string.ArticleListSummary_Position)
        else
            status = this.resources.getString(R.string.ArticleListSummary_Positions)

        binding.ArticleListFooter.text = String.format(status, articleList.count())
    }


    fun onOpenArticleDetails(articleId: Int)
    {
        listViewState = binding.ArticleList.layoutManager?.onSaveInstanceState()

        val intent = Intent(this, ArticleDetailsActivity::class.java)
        intent.putExtra("articleId", articleId)
        detailLauncher.launch(intent)
    }

    fun onCreateArticle()
    {
        val intent = Intent(this, ArticleDetailsActivity::class.java)
        detailLauncher.launch(intent)
    }

    fun shareList()
    {
        /* TODO
        if (MainActivity.IsGooglePlayPreLaunchTestMode)
        {
            return;
        }
        */

        var text = ""
        val list = Database.getArticleList(text)

        for(article in list)
        {
            if (article.heading.isNotEmpty())    text += article.heading + "\n"
            if (article.subHeading.isNotEmpty()) text += article.subHeading + "\n"
            if (article.notesText.isNotEmpty())  text += article.notesText + "\n"
            text += "\n"
        }

        text += binding.ArticleListFooter.text

        val now = LocalDateTime.now()

        val subject = String.format("%s - %s",
            this.resources.getString(R.string.Main_Button_Artikelangaben),
            now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "text/plain"

        startActivity(intent)
    }
}
