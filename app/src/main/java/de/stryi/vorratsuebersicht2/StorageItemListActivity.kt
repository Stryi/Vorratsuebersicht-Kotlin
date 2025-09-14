package de.stryi.vorratsuebersicht2

import android.os.Bundle
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

        //this.setSupportActionBar(binding.StorageItemListAppBar)
        this.setSupportActionBar(binding.toolbar)
        //setSupportActionBar(findViewById(R.id.toolbar))

        binding.StorageItemListText.text = "Das ist ein Test."

        this.title = "TEST"

        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
    }
}