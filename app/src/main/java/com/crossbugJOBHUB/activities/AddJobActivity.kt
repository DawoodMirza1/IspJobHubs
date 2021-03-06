package com.crossbugJOBHUB.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.arbazmateen.validator.validator
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.retrofit.interfaces.categoryService
import com.crossbugJOBHUB.retrofit.interfaces.jobService
import com.crossbugJOBHUB.retrofit.models.Category
import com.crossbugJOBHUB.retrofit.response.APIResponseMsg
import kotlinx.android.synthetic.main.activity_add_job.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddJobActivity : AppCompatActivity() {

    private lateinit var wait: AlertDialog
    lateinit var cat: Category
    private var list = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_job)

        wait = waitDialog(this)

        category.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_UP) {
                showCategoryDialog()
            }
            false
        }

        loginBtn.setOnClickListener {

            val validTitle = _title.validator(layout_title).required().validate()
            val validType = _type.validator(layout_type).required().validate()
            val validSalary = _salary.validator(layout_salary).required().validate()
            val validEducation = _education.validator(layout_education).required().validate()
            val validDescp = description.validator(layout_description).required().validate()

            if (validTitle && validDescp && validType && validSalary && validEducation) {

                saveJob()

            }

        }

        getCategories()

    }

    private fun getCategories() {
        InternetCheck { internet ->
            if (internet) {

                categoryService().getCategories().enqueue(object : Callback<MutableList<Category>?> {
                    override fun onResponse(call: Call<MutableList<Category>?>, response: Response<MutableList<Category>?>) {
                        if (response.isSuccessful && response.body() != null) {
                            list = response.body()!!
                        }
                    }

                    override fun onFailure(call: Call<MutableList<Category>?>, t: Throwable) {}
                })

            } else {
                noInternetFragment()
            }
        }

    }

    private fun showCategoryDialog() {
        SingleSelectListDialog(this@AddJobActivity,
            list, "Select Category")
            .setItemClickListener { item, _, _ ->
                category.setText(item.title)
                cat = item
            }
            .show()
    }

    fun saveJob() {
        InternetCheck { internet ->
            if (internet) {
                wait.show()
                jobService().saveJob(_title.text(), _type.text(), _salary.text(), _education.text(), description.text(), cat.id).enqueue(object : Callback<APIResponseMsg?> {
                    override fun onResponse(call: Call<APIResponseMsg?>, response: Response<APIResponseMsg?>) {
                        wait.dismiss()
                        if (response.isSuccessful && response.body() != null && response.body()?.success == 1) {
                            toastLong("Job Saved")
                            onBackPressed()
                        } else {
                            errorDialog(this@AddJobActivity, "Error", "Failed to saved Job!", true).show()
                        }
                    }

                    override fun onFailure(call: Call<APIResponseMsg?>, t: Throwable) {
                        wait.dismiss()
                        errorDialog(this@AddJobActivity, "Error", "Failed to saved Job!", true).show()
                    }
                })

            } else {
                noInternetFragment()
            }
        }
    }
}
