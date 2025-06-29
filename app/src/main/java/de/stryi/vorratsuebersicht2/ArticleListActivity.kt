package de.stryi.vorratsuebersicht2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ArticleListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article_list)

        val data = mutableListOf<String>()

        val articles =  Database.getArticleList()
        for (article in articles) {
            data.add(article)
        }

        val listView = findViewById<ListView>(R.id.test)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        listView.adapter = adapter
    }
}