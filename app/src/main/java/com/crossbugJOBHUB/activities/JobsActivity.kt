package com.crossbugJOBHUB.activities

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.Divider
import com.crossbugJOBHUB.commons.SimpleRecyclerAdaptor
import com.crossbugJOBHUB.commons.waitDialog
import com.crossbugJOBHUB.retrofit.models.Job
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_jobs.*

class JobsActivity : AppCompatActivity() {

    private var adaptor: SimpleRecyclerAdaptor<Job>? = null
    private var list = mutableListOf<Job>()

    private lateinit var wait: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        wait = waitDialog(this)

        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    return if (newText.isNotEmpty()) {

                        val filtered = list
                            .asSequence()
                            .filter { it.title.toLowerCase().contains(newText.toLowerCase()) }
                            .toMutableList()

                        if(adaptor != null)
                            adaptor!!.changeDataList(filtered)

                        true
                    } else {

                        if(adaptor != null)
                            adaptor!!.changeDataList(list)

                        false
                    }
                }
                return false
            }
        })

        recycler_view.addItemDecoration(Divider(this))

    }

    fun initAdaptor() {
        adaptor = SimpleRecyclerAdaptor.Builder<Job>(this@JobsActivity)
            .setLayout(android.R.layout.simple_expandable_list_item_1)
            .addView(android.R.id.text1)
            .setBindViewListener { view, item, position, viewMap ->

            }
            .setItemClickListener { item, position ->

            }
            .build()
        recycler_view.adapter = adaptor
    }

    override fun onBackPressed() {
        when {
            search_view.isSearchOpen -> search_view.closeSearch()
            else -> super.onBackPressed()
        }
    }

}
