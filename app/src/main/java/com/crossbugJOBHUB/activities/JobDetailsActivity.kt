package com.crossbugJOBHUB.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arbazmateen.prefs.Prefs
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.retrofit.interfaces.jobService
import com.crossbugJOBHUB.retrofit.models.Job
import com.crossbugJOBHUB.retrofit.response.APIResponseMsg
import kotlinx.android.synthetic.main.activity_job_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobDetailsActivity : AppCompatActivity() {

    companion object {
        const val KEY = "jobKey"
        const val pos = "pos"
    }

    private lateinit var wait: AlertDialog

    private var position = -1
    private var job: Job? = null

    private val userId by lazy {
        Prefs(this@JobDetailsActivity).getLong(Keys.USER_ID, 0L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        wait = waitDialog(this)

        job = getSerializable(KEY)
        position = getIntValue(pos)

        if (job != null) {

            jb_title.text = job?.title
            jb_type.text = "Type: " + job?.type
            jb_salary.text = "Salary: " + job?.salary
            jb_education.text = "Education: " + job?.education
            jb_descp.text = "Description:\n" + job?.description

        }

        jb_apply.setOnClickListener {
            applyJob()
        }

    }

    fun applyJob() {
        InternetCheck { internet ->
            if (internet) {
                wait.show()
                jobService().applyJob(job?.id!!, userId).enqueue(object : Callback<APIResponseMsg?> {
                    override fun onResponse(call: Call<APIResponseMsg?>, response: Response<APIResponseMsg?>) {
                        wait.dismiss()
                        if (response.isSuccessful && response.body() != null && response.body()?.success == 1) {

                            val intent = Intent()
                            intent.putExtra(pos, position)
                            setResult(Activity.RESULT_OK, intent)
                            finish()

                        } else {
                            errorDialog(this@JobDetailsActivity, "Error", "Failed to apply this Job!", true).show()
                        }
                    }

                    override fun onFailure(call: Call<APIResponseMsg?>, t: Throwable) {
                        wait.dismiss()
                        errorDialog(this@JobDetailsActivity, "Error", "Failed to apply this Job!", true).show()
                    }
                })

            } else {
                noInternetFragment()
            }
        }

    }
}
