package de.stryi.vorratsuebersicht2

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import de.stryi.vorratsuebersicht2.database.Database
import de.stryi.vorratsuebersicht2.databinding.ArticleListBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ArticleListActivity : AppCompatActivity() {

    private var category: String  = ""
    private var subCategory: String  = ""
    private var withoutCategory: Boolean  = false
    private val notInStorage: Boolean = false
    private val notInShoppingList: Boolean = false
    private val eanCode: String? = null
    private var lastSearchText: String? = ""
    private var specialFilter = 0

    private var listViewState: Parcelable? = null
    private lateinit var binding: ArticleListBinding

    private val detailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //val data = result.data
                //val updatedArticleId = data?.getIntExtra("articleId", -1)

                showArticleList(lastSearchText)

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

        // ArticleListeSpecialFilter
        val filters = this.resources.getTextArray(R.array.ArticleListeSpecialFilter)
        binding.ArticleListFilter.text = filters[0]

        val categoryList = mutableListOf<String?>()
        categoryList.add(this.resources.getString(R.string.ArticleList_AllCategories))
        categoryList.add(this.resources.getString( R.string.ArticleList_NoCategories))
        categoryList.addAll(Database.getCategoryAndSubcategoryNames())

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.ArticleListCategories.adapter = dataAdapter
        binding.ArticleListCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position) as String
                spinnerCategoryItemSelected(position, selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Verhalten, wenn nichts ausgewählt ist
                spinnerCategoryItemSelected(0, "")
            }
        }

        binding.ArticleListFilterBanner.setOnClickListener {

            this.filterArticleList()
        }

        binding.ArticleListFilterClear.setOnClickListener {
            this.specialFilter = 0
            this.binding.ArticleListFilter.text = ""
            this.binding.ArticleListFilterBanner.visibility = View.GONE
        }

        showArticleList()
    }

    private fun filterArticleList()
    {
        // ArticleListeSpecialFilter
        val actions = this.resources.getTextArray(R.array.ArticleListeSpecialFilter)

        val builder = AlertDialog.Builder(this)
        builder.setItems(actions) { _, which ->
            this.specialFilter = which + 1

            binding.ArticleListFilter.text = actions[which]
            binding.ArticleListFilterBanner.visibility = View.VISIBLE

            this.showArticleList()
        }
        builder.show()

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
                showArticleList(newText)
                lastSearchText = newText.orEmpty()
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ArticleList_Menu_Add -> {
                onCreateArticle()
                return true
            }
            R.id.ArticleList_Menu_Filter -> {
                // Handle settings
                this.filterArticleList()
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
        val articleList = Database.getArticleList(
            this.category,
            this.subCategory,
            this.eanCode,
            this.notInStorage,
            this.notInShoppingList,
            this.withoutCategory,
            this.specialFilter,
            text)

        val adapter = ArticleListViewAdapter(articleList, this::onOpenArticleDetails)

        binding.ArticleList.layoutManager = LinearLayoutManager(this)
        binding.ArticleList.adapter = adapter

        val status = if (articleList.count() == 1)
            this.resources.getString(R.string.ArticleListSummary_Position)
        else
            this.resources.getString(R.string.ArticleListSummary_Positions)

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
        listViewState = binding.ArticleList.layoutManager?.onSaveInstanceState()

        val intent = Intent(this, ArticleDetailsActivity::class.java)
        detailLauncher.launch(intent)
    }

     fun spinnerCategoryItemSelected(position: Int, categoryText: String)
     {
         var newCategoryName = ""
         var newSubCategoryName  = ""
         var withoutCategory = false

         var name = categoryText

         if (position == 1)
         {
             withoutCategory = true
         }

         if (position > 1)
         {
             if (name.startsWith("  - "))    // Ist das ein SubCategory?
             {
                 name = name.substring(4)  // Mach aus "  - Gulasch" ein "Gulasch"
                 newSubCategoryName = name
             } else
             {
                 newCategoryName = name
             }
         }
         if ((newCategoryName != this.category) || (newSubCategoryName != this.subCategory) || withoutCategory != this.withoutCategory)
         {
             this.category        = newCategoryName
             this.subCategory     = newSubCategoryName
             this.withoutCategory = withoutCategory

             this.showArticleList(this.lastSearchText)
         }
     }


    fun shareList()
    {
        if (MainActivity.IsGooglePlayPreLaunchTestMode)
        {
            return
        }

        var text = ""
        val list = Database.getArticleList(
            this.category,
            this.subCategory,
            this.eanCode,
            this.notInStorage,
            this.notInShoppingList,
            this.withoutCategory,
            this.specialFilter,
            lastSearchText)

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
