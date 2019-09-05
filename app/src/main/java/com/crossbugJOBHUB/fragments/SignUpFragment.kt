package com.crossbugJOBHUB.fragments


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arbazmateen.validator.validator

import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.activities.StartActivity
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.commons.InternetCheck
import com.crossbugJOBHUB.retrofit.interfaces.userService
import com.crossbugJOBHUB.retrofit.models.User
import com.crossbugJOBHUB.retrofit.response.APIResponces
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.layout_username
import kotlinx.android.synthetic.main.fragment_sign_up.loginBtn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpFragment : Fragment() {

    companion object {
        const val TAG = "SignUpFragment"
    }

    private lateinit var mContext: Context
    private lateinit var mActivity: StartActivity

    private lateinit var wait: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wait = waitDialog(mContext)

        loginBtn.setOnClickListener {
            validate()
        }

    }

    private fun validate() {
        val isNameOk = name.validator(layout_name).required().validate()
        val idFatherNameOk = father_name.validator(layout_father_name).required().validate()
        val isUsernameOk = username.validator(layout_username).required().validate()
        val isEmailOk = email.validator(layout_email).required().validateEmail(true).validate()
        val isPasswordOk = password.validator(layout_password).required().validate()
        val isConfirmPasswordOk = confirm_password.validator(layout_confirm_password).required().validate()
        val passMatch = password.text() == confirm_password.text()
        if (!passMatch) layout_password.error = "Password not Matched!"
        else layout_password.isErrorEnabled = false
        val isMatricOk = matric_per.validator(layout_matric_per).required().validate()
        val isInterOk = inter_per.validator(layout_inter_per).required().validate()
        val isAgeOk = age.validator(layout_age).required().validateIntegers(minValue = 18, maxValue = 80).validate()
        val isCGPAOk = cpga.validator(layout_cgpa).required().validateDecimals(minValue = 1.0, maxValue = 4.0).validate()

        if (isNameOk && idFatherNameOk && isUsernameOk && isEmailOk &&
            isPasswordOk && isConfirmPasswordOk && isMatricOk && isInterOk &&
            isAgeOk && isCGPAOk && passMatch) {
            submit()
        }
    }

    private fun submit() {
        val nameValue = name.text()
        val fatherNameValue = father_name.text()
        val usernameValue = username.text()
        val emailValue = email.text()
        val passwordValue = password.text()
        val gander = (gander.checkedRadioButtonId == R.id.g_male).toInt()
        val matricPerValue = matric_per.text()
        val interPerValue = inter_per.text()
        val ageValue = age.int()
        val cgpaValue = cpga.double()
        val addressValue = address.text()

        val user = User(0, nameValue, fatherNameValue, usernameValue, emailValue, gander, "",
            matricPerValue, interPerValue, ageValue, cgpaValue, "", addressValue, passwordValue)

        saveUser(user)

    }

    private fun saveUser(user: User) {
        InternetCheck { internet ->
            if (internet) {
                wait.show()

                userService().saveUser(user).enqueue(object: Callback<APIResponces<User>?> {
                    override fun onResponse(call: Call<APIResponces<User>?>, response: Response<APIResponces<User>?>) {
                        wait.dismiss()
                        if (response.isSuccessful && response.body() != null &&
                            response.body()?.success == 1) {
                            if (response.body()?.code == 1) {
                                toastLong("User Saved")
                                mActivity.supportFragmentManager.beginTransaction()
                                    .replace(android.R.id.content, LoginFragment())
                                    .commit()
                            } else {
                                errorDialog(mContext, "Response From Server",
                                    response.body()?.message ?: "Invalid Response from server", true).show()
                            }
                        } else {
                            errorDialog(mContext, "Failed", "Failed to save User data!", true).show()
                        }
                    }

                    override fun onFailure(call: Call<APIResponces<User>?>, t: Throwable) {
                        wait.dismiss()
                        errorDialog(mContext, "Failed", "Failed to save User data!", true).show()
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
