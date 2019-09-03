package com.crossbugJOBHUB.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.Divider
import com.crossbugJOBHUB.commons.SimpleRecyclerAdaptor
import com.crossbugJOBHUB.commons.setHighlightedText
import com.crossbugJOBHUB.commons.waitDialog
import com.crossbugJOBHUB.retrofit.models.Job
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_jobs.*

class JobsActivity : AppCompatActivity() {

    private var queryText = ""
    private var adaptor: SimpleRecyclerAdaptor<Job>? = null
    private var list = mutableListOf<Job>(
        Job(1, "1 - Android Developer", "Details for job requirements and specifications"),
        Job(1, "2 - Android Developer", "Details for job requirements and specifications"),
        Job(1, "3 - Android Developer", "Details for job requirements and specifications"),
        Job(1, "4 - Android Developer", "Details for job requirements and specifications"),
        Job(1, "5 - Android Developer", "Details for job requirements and specifications"),
        Job(1, "6 - Android Developer", "Details for job requirements and specifications"),
        Job(1, "7 - Android Developer", "Details for job requirements and specifications"),
        Job(1, "8 - Android Developer", "Details for job requirements and specifications"),
        Job(1, "9 - Android Developer", "Details for job requirements and specifications")
        )

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

                        queryText = newText

                        val filtered = list
                            .asSequence()
                            .filter { it.title.toLowerCase().contains(newText.toLowerCase()) }
                            .toMutableList()

                        if(adaptor != null)
                            adaptor!!.changeDataList(filtered)

                        true
                    } else {
                        queryText = ""
                        if(adaptor != null)
                            adaptor!!.changeDataList(list)

                        false
                    }
                }
                return false
            }
        })

        recycler_view.addItemDecoration(Divider(this))

        empty_progress?.visibility = View.GONE
        recycler_view?.visibility = View.VISIBLE
        empty_layout?.visibility = View.GONE

        initAdaptor()
    }

    fun applyJob(id: Long, position: Int) {

    }

    fun initAdaptor() {
        adaptor = SimpleRecyclerAdaptor.Builder<Job>(this@JobsActivity)
            .setDataList(list)
            .setLayout(R.layout.job_item_layout)
            .addViews(R.id.title, R.id.description, R.id.apply)
            .setBindViewListener { _, item, position, viewMap ->
                val title = viewMap[R.id.title] as TextView
                val descp = viewMap[R.id.description] as TextView
                val apply = viewMap[R.id.apply] as Button

                title.setHighlightedText(this@JobsActivity, item.title, queryText)
                descp.text = item.description

                apply.setOnClickListener {
                    applyJob(item.id, position)
                }
            }
            .setItemClickListener { item, position ->
                applyJob(item.id, position)
            }
            .build()
        recycler_view.adapter = adaptor
        recycler_view.addItemDecoration(Divider(this@JobsActivity))
    }

    override fun onBackPressed() {
        when {
            search_view.isSearchOpen -> search_view.closeSearch()
            else -> super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        search_view.setMenuItem(menu?.findItem(R.id.action_search))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}
