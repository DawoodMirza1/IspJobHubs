package com.crossbugJOBHUB.fragments


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arbazmateen.prefs.Prefs
import com.arbazmateen.validator.validator

import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.activities.MainActivity
import com.crossbugJOBHUB.activities.StartActivity
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.commons.InternetCheck
import com.crossbugJOBHUB.retrofit.interfaces.userService
import com.crossbugJOBHUB.retrofit.models.User
import com.crossbugJOBHUB.retrofit.response.APIResponces
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    private lateinit var mContext: Context
    private lateinit var mActivity: StartActivity

    private lateinit var wait: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Prefs(mContext).get(Keys.LOGIN, false)) {
            checkUser()
        }

        wait = waitDialog(mContext, "Authenticating...")

        loginBtn.setOnClickListener {

            val validUsername = username.validator(layout_username).required().validate()
            val validPassword = password.validator(layout_password).required().validate()

            if(validUsername && validPassword) {

                InternetCheck { internet ->
                    if(internet) {
                        wait.show()

                        userService().authenticate(username.text(), password.text()).enqueue(object:
                            Callback<APIResponces<User>?> {
                            override fun onResponse(call: Call<APIResponces<User>?>, response: Response<APIResponces<User>?>) {
                                if(response.isSuccessful && response.body() != null && response.body()!!.success == 1) {
                                    when(response.body()!!.code) {
                                        APICode.OK -> {

                                            val data = response.body()!!.data
                                            Prefs(mContext).put(Keys.USER_ID, data.id)
                                            Prefs(mContext).put(Keys.NAME, data.name)
                                            Prefs(mContext).put(Keys.USERNAME, username.text())
                                            Prefs(mContext).put(Keys.PASSWORD, password.text().encrypt())
                                            Prefs(mContext).put(Keys.LOGIN, true)

                                            Prefs(mContext).put(Keys.EMAIL, data.email ?: "")
                                            Prefs(mContext).put(Keys.PROFILE_IMAGE, data.imageUrl ?: "")

                                            wait.dismiss()
                                            startActivity(Intent(mContext, MainActivity::class.java))
                                            mActivity.finish()

                                        }
                                        APICode.NOT_VALID -> {
                                            wait.dismiss()
                                            errorDialog(mContext, "Invalid Credentials", "Username/Password is not valid, Please check and try again!", true).show()
                                        }
                                    }

                                } else {
                                    wait.dismiss()
                                    info("else")
                                    errorDialog(mContext, "Invalid Response", "Invalid response from server, Please try again later!", true).show()
                                }
                            }

                            override fun onFailure(call: Call<APIResponces<User>?>, t: Throwable) {
                                wait.dismiss()
                                info("failure")
                                errorDialog(mContext, "Invalid Response", "Invalid response from server, Please try again later!", true).show()
                            }
                        })
                    } else {
                        noInternetFragment()
                    }
                }

            }

        }

        signUpBtn.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, SignUpFragment())
                .commit()
        }
    }

    private fun checkUser() {
        InternetCheck { internet ->
            if (internet) {

                wait.show()

                userService().authenticate(
                    Prefs(mContext).get(Keys.USERNAME, "") ?: "",
                    (Prefs(mContext).get(Keys.PASSWORD, "") ?: "").decrypt()
                ).enqueue(object : Callback<APIResponces<User>?> {
                    override fun onResponse(call: Call<APIResponces<User>?>, response: Response<APIResponces<User>?>) {
                        if (response.isSuccessful && response.body() != null && response.body()!!.success == 1) {
                            when(response.body()!!.code) {
                                APICode.OK -> {

                                    val data = response.body()!!.data
                                    Prefs(mContext).put(Keys.USER_ID, data.id)
                                    Prefs(mContext).put(Keys.NAME, data.name)
                                    Prefs(mContext).put(Keys.USERNAME, username.text())
                                    Prefs(mContext).put(Keys.PASSWORD, password.text().encrypt())
                                    Prefs(mContext).put(Keys.LOGIN, true)

                                    Prefs(mContext).put(Keys.EMAIL, data.email ?: "")
//                                            Prefs(mContext).put(Keys.PROFILE_IMAGE, data.imageUrl ?: "")

                                    wait.dismiss()
                                    startActivity(Intent(mContext, MainActivity::class.java))
                                    mActivity.finish()

                                }
                                APICode.NOT_VALID -> {
                                    wait.dismiss()
                                    errorDialog(
                                        mContext,
                                        "Invalid Credentials",
                                        "Username/Password is not valid, Please check and try again!",
                                        true
                                    ).show()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<APIResponces<User>?>, t: Throwable) {
                        wait.dismiss()
                    }
                })

            } else {
                noInternetFragment()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        if (context is StartActivity) {
            mActivity = context
        }
    }
}
