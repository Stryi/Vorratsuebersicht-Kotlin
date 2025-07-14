package de.stryi.vorratsuebersicht2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.stryi.vorratsuebersicht2.database.Article

class ArticleListViewAdapter(private val items: List<Article>) :
    RecyclerView.Adapter<ArticleListViewAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.ArticleListView_Heading)
        val eanText: TextView = view.findViewById(R.id.ArticleListView_SubHeading)
        //val imageView: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.article_list_view, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = items[position]
        holder.nameText.text = article.name
        holder.eanText.text = article.eanCode ?: "Kein EAN"
        //holder.imageView.setImageResource(R.drawable.ic_placeholder) // Platzhalterbild
    }

    override fun getItemCount(): Int = items.size
}
