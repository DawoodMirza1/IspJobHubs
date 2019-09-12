package com.crossbugJOBHUB.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arbazmateen.validator.validator
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.commons.InternetCheck
import com.crossbugJOBHUB.retrofit.interfaces.jobService
import com.crossbugJOBHUB.retrofit.response.APIResponseMsg
import kotlinx.android.synthetic.main.activity_add_job.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddJobActivity : AppCompatActivity() {

    private lateinit var wait: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_job)

        wait = waitDialog(this)

        loginBtn.setOnClickListener {

            val validTitle = _title.validator(layout_title).required().validate()
            val validDescp = description.validator(layout_description).required().validate()

            if (validTitle && validDescp) {

                saveJob()

            }

        }


    }

    fun saveJob() {
        InternetCheck { internet ->
            if (internet) {
                wait.show()
                jobService().saveJob(_title.text(), description.text()).enqueue(object : Callback<APIResponseMsg?> {
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
