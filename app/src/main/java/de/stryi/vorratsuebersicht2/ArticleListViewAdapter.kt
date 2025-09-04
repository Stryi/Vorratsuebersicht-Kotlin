package de.stryi.vorratsuebersicht2

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import de.stryi.vorratsuebersicht2.database.Article
import de.stryi.vorratsuebersicht2.database.Database

class ArticleListViewAdapter(private val articles: List<Article>,
                             private val onItemClick: (articleId: Int) -> Unit) :
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
        val option: TextView = view.findViewById(R.id.ArticleListView_Option)
        val articleListViewOption: TextView = view.findViewById(R.id.ArticleListView_Option)
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
        holder.storageQuantity.visibility = if (article.isInStorage) View.VISIBLE else View.GONE
        holder.storageQuantity.text       = article.storageQuantity

        // Mehr Platz für Notizen
        if (!article.isOnShoppingList && !article.isInStorage)
        {
            holder.onShoppingList.visibility   = View.GONE
            holder.shoppingQuantity.visibility = View.GONE
            holder.isInStorage.visibility      = View.GONE
            holder.storageQuantity.visibility  = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(article.articleId)
        }

        holder.itemView.setOnLongClickListener {
            val articleId = holder.itemView.tag as Int
            showContextMenu(holder, articleId)
            true
        }

        holder.articleListViewOption.setOnClickListener {
            val articleId = holder.itemView.tag as Int
            showContextMenu(holder, articleId)
        }

        val articleImage = Database.getArticleImage(article.articleId, false)

        if (articleImage == null)
        {
            holder.image.setImageResource(R.drawable.photo_camera_24px)
            holder.image.alpha = 0.2.toFloat()
            holder.image.setOnClickListener { }
        }
        else
        {
            val bitmap = BitmapFactory.decodeByteArray(articleImage.imageSmall, 0, articleImage.imageSmall!!.size)
            holder.image.setImageBitmap(bitmap)
            holder.image.alpha = 1.toFloat()
            holder.image.setOnClickListener {
                val intent = Intent(holder.image.context, ArticleImageActivity::class.java)
                intent.putExtra("ArticleId", article.articleId)
                holder.image.context.startActivity(intent)
            }
        }
    }

    fun showContextMenu(holder: ArticleViewHolder, articleId: Int) {
        val popupMenu = PopupMenu(holder.itemView.context, holder.option)
        popupMenu.menuInflater.inflate(R.menu.article_list_contextmenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            val articleId = holder.itemView.tag
            when (menuItem.itemId) {
                R.id.ArticleList_ContextMenu_Lagerbestand -> {
                    Toast.makeText(holder.itemView.context, "TODO: Lagerbestand aufrufen", Toast.LENGTH_SHORT).show()
                    /*
                    var storageDetails = new Intent(this, typeof(StorageItemQuantityActivity));
                    storageDetails.PutExtra("ArticleId", selectedItem.ArticleId);

                    this.SaveListState();
                    this.StartActivityForResult(storageDetails, 20);
                    */
                    true
                }
                R.id.ArticleList_ContextMenu_AufEinkaufszettel -> {
                    Toast.makeText(holder.itemView.context, "TODO: Auf Einkaufsliste aufrufen", Toast.LENGTH_SHORT).show()
                    /*
                    AddToShoppingListDialog.ShowDialog(
                        this,
                        selectedItem.ArticleId,
                        null, null, this.RefreshArticleList);
                    */
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun getItemCount(): Int = articles.size
}
