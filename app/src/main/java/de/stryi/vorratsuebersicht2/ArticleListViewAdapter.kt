package de.stryi.vorratsuebersicht2

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.stryi.vorratsuebersicht2.database.Article
import de.stryi.vorratsuebersicht2.database.Database

class ArticleListViewAdapter(private val articles: List<Article>) :
    RecyclerView.Adapter<ArticleListViewAdapter.ArticleViewHolder>()
{
    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val heading: TextView = view.findViewById(R.id.ArticleListView_Heading)
        val subHeading: TextView = view.findViewById(R.id.ArticleListView_SubHeading)
        val notesText:  TextView = view.findViewById(R.id.ArticleListView_Notes)
        val onShoppingList: ImageView = view.findViewById(R.id.ArticleListView_OnShoppingList)
        val shoppingQuantity: TextView = view.findViewById(R.id.ArticleListView_ShoppingQuantity)
        val isInStorage: ImageView = view.findViewById(R.id.ArticleListView_IsInStorage)
        val storageQuantity: TextView = view.findViewById(R.id.ArticleListView_StorageQuantity)
        val image: ImageView = view.findViewById(R.id.ArticleListView_Image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.article_list_view, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]

        holder.itemView.tag = article.articleId

        holder.heading.text    = article.heading
        holder.subHeading.text = article.subHeading
        holder.notesText.text  = article.notesText
        holder.notesText.visibility = if (article.notesText.isEmpty()) View.GONE else View.VISIBLE

        holder.onShoppingList.visibility   = if (article.isOnShoppingList) View.VISIBLE else View.GONE
        holder.shoppingQuantity.visibility = if (article.isOnShoppingList) View.VISIBLE else View.GONE
        holder.shoppingQuantity.text       = article.shoppingQuantity

        holder.isInStorage.visibility     = if (article.isInStorage) View.VISIBLE else View.INVISIBLE
        holder.storageQuantity.visibility = if (article.isInStorage) View.VISIBLE else View.INVISIBLE
        holder.storageQuantity.text       = article.storageQuantity

        // Mehr Platz für Notizen(?)
        if (!article.isOnShoppingList && !article.isInStorage)
        {
            holder.onShoppingList.visibility   = View.GONE
            holder.shoppingQuantity.visibility = View.GONE
            holder.isInStorage.visibility      = View.GONE
            holder.storageQuantity.visibility  = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ArticleDetailsActivity::class.java)
            intent.putExtra("articleId", article.articleId)
            holder.itemView.context.startActivity(intent)
        }
        
        val byteArray = Database.getArticleImage(article.articleId, false)

        if (byteArray.isEmpty())
        {
            holder.image.setImageResource(R.drawable.photo_camera_24px)
            holder.image.alpha = 0.2.toFloat()
        }
        else
        {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            holder.image.setImageBitmap(bitmap)
            holder.image.alpha = 1.toFloat()
        }


    }

    override fun getItemCount(): Int = articles.size
}
