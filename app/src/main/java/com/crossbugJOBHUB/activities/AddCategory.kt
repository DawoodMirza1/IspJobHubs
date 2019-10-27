package com.crossbugJOBHUB.activities

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arbazmateen.validator.validator
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.commons.InternetCheck
import com.crossbugJOBHUB.retrofit.interfaces.categoryService
import com.crossbugJOBHUB.retrofit.interfaces.jobService
import com.crossbugJOBHUB.retrofit.response.APIResponseMsg
import kotlinx.android.synthetic.main.activity_add_category.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCategory : AppCompatActivity() {

    private lateinit var wait: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        wait = waitDialog(this)

        addCat.setOnClickListener {
            val validTitle = _title.validator(layout_title).required().validate()

            if (validTitle) {
                saveCategory()
            }
        }

    }

    private fun saveCategory() {
        InternetCheck { internet ->
            if (internet) {
                wait.show()
                categoryService().saveCategory(0L, _title.text()).enqueue(object : Callback<APIResponseMsg?> {
                    override fun onResponse(call: Call<APIResponseMsg?>, response: Response<APIResponseMsg?>) {
                        wait.dismiss()
                        if (response.isSuccessful && response.body() != null && response.body()?.success == 1) {
                            toastLong("Category Saved")
                            onBackPressed()
                        } else {
                            errorDialog(this@AddCategory, "Error", "Failed to saved Category!", true).show()
                        }
                    }

                    override fun onFailure(call: Call<APIResponseMsg?>, t: Throwable) {
                        wait.dismiss()
                        errorDialog(this@AddCategory, "Error", "Failed to saved Job!", true).show()
                    }
                })

            } else {
                noInternetFragment()
            }
        }
    }
}
