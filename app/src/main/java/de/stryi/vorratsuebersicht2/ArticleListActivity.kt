package de.stryi.vorratsuebersicht2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ArticleListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article_list)

        val listView = findViewById<ListView>(R.id.test)
        val data = listOf(
            "Appell", "Banana", "Orange", "Kiwi",
            "Mango", "Pineapple", "Strawberry", "Blueberry", "Raspberry",
            "Blackberry", "Grapes", "Watermelon", "Cantaloupe", "Honeydew",
            "Peach", "Plum", "Apricot", "Cherry", "Lemon", "Lime",
            "Grapefruit", "Papaya", "Guava", "Passion fruit", "Pomegranate",
            "Fig", "Date", "Coconut", "Lychee", "Dragon fruit",
            "Starfruit", "Tangerine", "Nectarine", "Cranberry"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        listView.adapter = adapter

    }
}