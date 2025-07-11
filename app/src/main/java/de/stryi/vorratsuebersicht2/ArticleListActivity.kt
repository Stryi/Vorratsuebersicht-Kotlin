package de.stryi.vorratsuebersicht2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ArticleListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_list)

        val articles = Database.getArticleList()

        val listView = findViewById<ListView>(R.id.ArticleList)

        val adapter = object : ArrayAdapter<Article>(this, R.layout.activity_article_list_view, articles) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_article_list_view, parent, false)
                val item = getItem(position)


                view.findViewById<TextView>(R.id.ArticleListView_Heading).text = item?.name
                view.findViewById<TextView>(R.id.ArticleListView_SubHeading).text = item?.subHeading

                val image = Database.GetArticleImage(item?.articleId, false);

                if (image.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                    view.findViewById<ImageView>(R.id.ArticleListView_Image).setImageBitmap(bitmap)
                }

                return view
            }
        }
        listView.adapter = adapter

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
    }
}