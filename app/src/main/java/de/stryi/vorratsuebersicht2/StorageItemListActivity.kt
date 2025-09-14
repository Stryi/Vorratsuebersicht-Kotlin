package de.stryi.vorratsuebersicht2

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.View
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.databinding.StorageItemList2Binding

class StorageItemListActivity : AppCompatActivity() {

    private lateinit var binding: StorageItemList2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = StorageItemList2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setSupportActionBar(binding.StorageItemListAppBar)

        binding.StorageItemListText.text = "Das ist ein Test."

        this.title = "TEST"

        //binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
    }

    /*
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?)
    {
        super.onCreateContextMenu(menu, v, menuInfo)
    }
    */

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.storage_item_list_menu, menu)
        return true
    }


}