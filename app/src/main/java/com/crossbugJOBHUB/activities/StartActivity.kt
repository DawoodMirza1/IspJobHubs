package com.crossbugJOBHUB.activities

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

                val dialog = waitDialog(this, "Authorizing...")
                dialog.show()

                userService().authenticate(
                    Prefs(this).get(Keys.USERNAME, "") ?: "",
                    (Prefs(this).get(Keys.PASSWORD, "") ?: "").decrypt()
                ).enqueue(object : Callback<APIResponces<User>?> {
                    override fun onResponse(call: Call<APIResponces<User>?>, response: Response<APIResponces<User>?>) {
                        dialog.dismiss()
                        if (response.isSuccessful && response.body() != null && response.body()!!.success == 1) {

                        } else {
                            loginFrag()
                        }
                    }

                    override fun onFailure(call: Call<APIResponces<User>?>, t: Throwable) {
                        dialog.dismiss()
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
