package com.crossbugJOBHUB.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.arbazmateen.prefs.Prefs
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.retrofit.interfaces.jobService
import com.crossbugJOBHUB.retrofit.models.Job
import com.crossbugJOBHUB.retrofit.response.APIResponcesList
import com.crossbugJOBHUB.retrofit.response.APIResponseMsg
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_jobs.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobsActivity : AppCompatActivity() {

    companion object {
        val category = "category"
        val CAT_ONE = 1
        val CAT_TWO = 2
        val CAT_THREE = 3
    }

    private var cateType = CAT_ONE
    private var queryText = ""
    private var adaptor: SimpleRecyclerAdaptor<Job>? = null
    private var list = mutableListOf<Job>()

    private val userId by lazy {
        Prefs(this@JobsActivity).getLong(Keys.USER_ID, 0L)
    }

    private lateinit var wait: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        wait = waitDialog(this)

        cateType = getIntValue(category, CAT_ONE)

        getJobs()

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

    }

    private fun getJobs(refresh: Boolean = false) {
        InternetCheck { internet ->
            if (internet) {

                empty_progress.visibility = View.VISIBLE
                recycler_view.visibility = View.GONE
                empty_layout.visibility = View.GONE

                jobService().getJobs(cateType, userId).enqueue(object : Callback<MutableList<Job>?> {
                    override fun onResponse(call: Call<MutableList<Job>?>, response: Response<MutableList<Job>?>) {
                        if (response.isSuccessful && response.body() != null) {
                            list = response.body()!!
                            if (list.isEmpty()) {
                                empty_progress?.visibility = View.GONE
                                recycler_view?.visibility = View.GONE
                                empty_layout?.visibility = View.VISIBLE
                                empty_text?.text = "Empty"
                            } else {
                                empty_progress?.visibility = View.GONE
                                recycler_view?.visibility = View.VISIBLE
                                empty_layout?.visibility = View.GONE

                                if(refresh) {
                                    if(adaptor != null)
                                        adaptor!!.changeDataList(list)
                                    else {
                                        initAdaptor()
                                    }
                                } else {
                                    initAdaptor()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<MutableList<Job>?>, t: Throwable) {
                        empty_progress?.visibility = View.GONE
                        recycler_view?.visibility = View.GONE
                        empty_layout?.visibility = View.VISIBLE
                        empty_text?.text = "Failed to load data from server.\nPlease try again."
                    }
                })

            } else {
                noInternetFragment()
            }
        }

    }

    fun applyJob(id: Long, position: Int) {
        InternetCheck { internet ->
            if (internet) {
                wait.show()
                jobService().applyJob(id, userId).enqueue(object : Callback<APIResponseMsg?> {
                    override fun onResponse(call: Call<APIResponseMsg?>, response: Response<APIResponseMsg?>) {
                        wait.dismiss()
                        if (response.isSuccessful && response.body() != null && response.body()?.success == 1) {
                            adaptor?.removeItemAtPosition(position)
                        } else {
                            errorDialog(this@JobsActivity, "Error", "Failed to apply this Job!", true).show()
                        }
                    }

                    override fun onFailure(call: Call<APIResponseMsg?>, t: Throwable) {
                        wait.dismiss()
                        errorDialog(this@JobsActivity, "Error", "Failed to apply this Job!", true).show()
                    }
                })

            } else {
                noInternetFragment()
            }
        }

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
