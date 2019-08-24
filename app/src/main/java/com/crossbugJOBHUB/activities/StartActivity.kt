package com.crossbugJOBHUB.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arbazmateen.prefs.Prefs
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.commons.InternetCheck
import com.crossbugJOBHUB.fragments.LoginFragment
import com.crossbugJOBHUB.retrofit.interfaces.userService
import com.crossbugJOBHUB.retrofit.models.User
import com.crossbugJOBHUB.retrofit.response.APIResponces
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        loginFrag()

        if (Prefs(this).get(Keys.LOGIN, false)) {
            checkUser()
        }
    }

    private fun checkUser() {
        InternetCheck { internet ->
            if (internet) {

                val wait = waitDialog(this, "Authorizing...")
                wait.show()

                userService().authenticate(
                    Prefs(this).get(Keys.USERNAME, "") ?: "",
                    (Prefs(this).get(Keys.PASSWORD, "") ?: "").decrypt()
                ).enqueue(object : Callback<APIResponces<User>?> {
                    override fun onResponse(call: Call<APIResponces<User>?>, response: Response<APIResponces<User>?>) {
                        if (response.isSuccessful && response.body() != null && response.body()!!.success == 1) {
                            when(response.body()!!.code) {
                                APICode.OK -> {

                                    val data = response.body()!!.data
                                    Prefs(this@StartActivity).put(Keys.USER_ID, data.id)
                                    Prefs(this@StartActivity).put(Keys.NAME, data.name)
                                    Prefs(this@StartActivity).put(Keys.USERNAME, username.text())
                                    Prefs(this@StartActivity).put(Keys.PASSWORD, password.text().encrypt())
                                    Prefs(this@StartActivity).put(Keys.LOGIN, true)

                                    Prefs(this@StartActivity).put(Keys.EMAIL, data.email ?: "")
//                                            Prefs(this@StartActivity).put(Keys.PROFILE_IMAGE, data.imageUrl ?: "")

                                    wait.dismiss()
                                    startActivity(Intent(this@StartActivity, MainActivity::class.java))
//                                                startActivity(Intent(mContext, DashboardActivity::class.java))
                                    this@StartActivity.finish()

                                }
                                APICode.NOT_VALID -> {
                                    wait.dismiss()
                                    errorDialog(
                                        this@StartActivity,
                                        "Invalid Credentials",
                                        "Username/Password is not valid, Please check and try again!",
                                        true
                                    ).show()
                                }
                            }
                        } else {
                            loginFrag()
                        }
                    }

                    override fun onFailure(call: Call<APIResponces<User>?>, t: Throwable) {
                        wait.dismiss()
                        loginFrag()
                    }
                })

            } else {
                noInternetFragment()
            }
        }
    }

    private fun loginFrag() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, LoginFragment())
            .commit()
    }

}
