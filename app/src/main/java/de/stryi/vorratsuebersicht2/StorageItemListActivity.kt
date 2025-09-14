package de.stryi.vorratsuebersicht2

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import de.stryi.vorratsuebersicht2.databinding.StorageItemListBinding

class StorageItemListActivity : AppCompatActivity() {

    private lateinit var binding: StorageItemListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = StorageItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setSupportActionBar(binding.StorageItemListAppBar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.storage_item_list_menu, menu)
        return true
    }


}