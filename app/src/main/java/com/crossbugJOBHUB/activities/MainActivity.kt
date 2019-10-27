package com.crossbugJOBHUB.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.arbazmateen.prefs.Prefs
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.retrofit.interfaces.categoryService
import com.crossbugJOBHUB.retrofit.models.Category
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {


    private var adaptor: SimpleRecyclerAdaptor<Category>? = null
    private var list = mutableListOf<Category>()

    private val status by lazy {
        Prefs(this@MainActivity).getString(Keys.STATUS, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCategories()

    }

    private fun getCategories(refresh: Boolean = false) {
        InternetCheck { internet ->
            if (internet) {

                list = mutableListOf()

                empty_progress.visibility = View.VISIBLE
                recycler_view.visibility = View.GONE
                empty_layout.visibility = View.GONE

                categoryService().getCategories().enqueue(object : Callback<MutableList<Category>?> {
                    override fun onResponse(call: Call<MutableList<Category>?>, response: Response<MutableList<Category>?>) {
                        if (response.isSuccessful && response.body() != null) {

                            if (status == "ADMIN") {
                                list.add(Category(-1L, "Add Category"))
                                list.add(Category(0L, "Add Job"))
                            }

                            list.addAll(response.body()!!)
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

                    override fun onFailure(call: Call<MutableList<Category>?>, t: Throwable) {
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

    fun initAdaptor() {
        adaptor = SimpleRecyclerAdaptor.Builder<Category>(this@MainActivity)
            .setDataList(list)
            .setLayout(R.layout.main_activity_category_item_layout)
            .addViews(R.id.title)
            .setBindViewListener { _, item, _, viewMap ->
                val title = viewMap[R.id.title] as TextView

                title.text = item.title

            }
            .setItemClickListener { item, _ ->
                if (item.id == -1L) {
                    startActivity(Intent(this@MainActivity, AddCategory::class.java))
                } else if (item.id == 0L) {
                    startActivity(Intent(this@MainActivity, AddJobActivity::class.java))
                } else {
                    val intent = Intent(this@MainActivity, JobsActivity::class.java)
                    intent.putExtra(JobsActivity.category, item.id)
                    startActivity(intent)
                }
            }
            .build()
        recycler_view.adapter = adaptor
        recycler_view.addItemDecoration(Divider(this@MainActivity))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.refresh -> {
                getCategories(true)
                true
            }
            R.id.profile -> {
                startActivity(Intent(this@MainActivity, UserProfileActivity::class.java))
                true
            }
            R.id.logout -> {
                Prefs(this@MainActivity).clear()
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}
