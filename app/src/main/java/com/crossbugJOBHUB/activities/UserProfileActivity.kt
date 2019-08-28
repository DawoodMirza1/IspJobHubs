package com.crossbugJOBHUB.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arbazmateen.prefs.Prefs
import com.arbazmateen.validator.REGEX_PASSWORD_WO_SPACE
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.*
import com.crossbugJOBHUB.commons.InternetCheck
import com.crossbugJOBHUB.retrofit.interfaces.userService
import com.crossbugJOBHUB.retrofit.response.APIResponces
import com.crossbugJOBHUB.retrofit.response.APIResponseMsg
import com.google.android.material.textfield.TextInputLayout
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_user_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UserProfileActivity : AppCompatActivity() {

    companion object {

        const val PERMISSION_REQUEST_CODE = 490
        const val IMAGE_REQUEST_CODE = 530

    }

    private var changes = false
    private var imageUpdated = false

    private var permissionsGranted = false
    private var imageUri: Uri? = null
    private var imageSelected = false

    private lateinit var wait: AlertDialog
    private val userId by lazy {
        Prefs(this@UserProfileActivity).getLong(Keys.USER_ID, 0L)
    }
    private val username by lazy {
        Prefs(this@UserProfileActivity).getString(Keys.USERNAME, "")
    }
    private val userEmail by lazy {
        Prefs(this@UserProfileActivity).getString(Keys.EMAIL, "")
    }
    private val name by lazy {
        Prefs(this@UserProfileActivity).getString(Keys.NAME, "")
    }
    private val userProfileImage by lazy {
        Prefs(this@UserProfileActivity).getString(Keys.PROFILE_IMAGE, "")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        wait = waitDialog(this)


        profile_username.text = username
        profile_email.text = userEmail
        profile_fullName.text = name

        Glide.with(this)
            .load(userProfileImage)
            .apply(RequestOptions().error(R.drawable.ic_person_square).placeholder(R.drawable.ic_person_square))
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    load_profile_image.hide()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    load_profile_image.hide()
                    return false
                }
            })
            .into(profile_image)

        profile_change_password.setOnClickListener {
            changePasswordDialog()
        }

        edit_password.setOnClickListener {
            changePasswordDialog()
        }

        select_image_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                verifyStorageCameraPermissions()
            } else {
                selectImageDialog()
            }
        }

        edit_fullName.setOnClickListener {
            singleInputDialog("Name", name)
        }

    }

    private fun singleInputDialog(title: String, value: String = "", icon: Int = R.drawable.ic_person) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_single_input, null)

        val layoutInput = view.findViewById<TextInputLayout>(R.id.layout_input)
        val editText = view.findViewById<EditText>(R.id.input_field)

        editText.hint = title
        editText.setText(value)
        editText.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)

        builder.setTitle(title)
        builder.setView(view)

        builder.setPositiveButton("Ok") { _, _ ->

        }

        builder.setNegativeButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        val inputDialog = builder.create()
        inputDialog.show()
        inputDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (editText.text.trim().isBlank()) {
                    layoutInput.error = "Required"
                } else {
                    layoutInput.isErrorEnabled = false
                    inputDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        inputDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            hideKeyboard(editText)
            inputDialog.dismiss()

            profile_fullName.text = editText.text.trim()
//            updateName()

        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

//    override fun onBackPressed() {
//        when {
//            imageUpdated -> {
//                val intent = Intent()
//                intent.putExtra(IMAGE_UPDATED, true)
//                intent.putExtra(IMAGE_URI, imageUri.toString())
//                setResult(Activity.RESULT_OK, intent)
//                finish()
//            }
//            changes -> {
//                setResult(Activity.RESULT_OK)
//                finish()
//            }
//            else -> super.onBackPressed()
//        }
//    }

//    private fun updateName() {
//        InternetCheck { internet ->
//            if (internet) {
//
//                val name = profile_fullName.text.toString()
//
//                userService().updateProfile(userId, "", "", name).enqueue(object : Callback<APIResponseMsg?> {
//                    override fun onResponse(call: Call<APIResponseMsg?>?, response: Response<APIResponseMsg?>?) {
//
//                        if (response != null && response.isSuccessful && response.body()?.success == 1 && response.body()?.code == 1) {
//
//                            Prefs(this@UserProfileActivity).put(Keys.NAME, name)
//                            changes = true
//                            toastShort("Name Updated.")
//                        } else {
//                            toastShort("Cannot update Name!")
//                        }
//
//                    }
//
//                    override fun onFailure(call: Call<APIResponseMsg?>?, t: Throwable?) {
//                        toastShort("Cannot update name!")
//                    }
//                })
//            } else {
//                noInternetFragment()
//            }
//        }
//    }

    private fun changePasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)

        val layoutCurrentPassInput = view.findViewById<TextInputLayout>(R.id.layout_current_pass_input)
        val layoutNewPassInput = view.findViewById<TextInputLayout>(R.id.layout_new_pass_input)
        val layoutConfirmPassInput = view.findViewById<TextInputLayout>(R.id.layout_confirm_pass_input)

        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val fields = view.findViewById<LinearLayout>(R.id.fields)
        val currentPass = view.findViewById<EditText>(R.id.input_current_pass)
        val newPass = view.findViewById<EditText>(R.id.input_new_pass)
        val confirmPass = view.findViewById<EditText>(R.id.input_confirm_pass)

        builder.setTitle("Change Password")
        builder.setView(view)

        builder.setPositiveButton("Ok") { _, _ ->

        }

        builder.setNegativeButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        val inputDialog = builder.create()
        inputDialog.show()
        inputDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        var curPass = false
        var nPass = false
        var cPass = false

        currentPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (currentPass.text.trim().length >= 8) {
//                    if (currentPass.text.trim().matches(Regex(PASSWORD_PATTERN))) {
                    curPass = true
                    layoutCurrentPassInput.isErrorEnabled = false
//                    } else {
//                        curPass = false
//                        layoutCurrentPassInput.error = getString(R.string.error_invalid_password)
//                    }
                } else {
                    curPass = false
                    layoutCurrentPassInput.error = "Min length 8"
                }
                inputDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = curPass && nPass && cPass
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        newPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (newPass.text.trim().length >= 8) {
                    if (newPass.text.trim().matches(Regex(REGEX_PASSWORD_WO_SPACE))) {
                        nPass = true
                        layoutNewPassInput.isErrorEnabled = false
                    } else {
                        nPass = false
                        layoutNewPassInput.error = "Invalid password"
                    }
                } else {
                    nPass = false
                    layoutNewPassInput.error = "Min length 8"
                }
                inputDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = curPass && nPass && cPass
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        confirmPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (confirmPass.text.trim().length >= 8) {
                    if (confirmPass.text.trim().matches(Regex(REGEX_PASSWORD_WO_SPACE))) {
                        if (confirmPass.text.trim().toString() == newPass.text.trim().toString()) {
                            cPass = true
                            layoutConfirmPassInput.isErrorEnabled = false
                        } else {
                            cPass = false
                            layoutConfirmPassInput.error = "Invalid confirm password"
                        }
                    } else {
                        cPass = false
                        layoutConfirmPassInput.error = "Invalid confirm password"
                    }
                } else {
                    cPass = false
                    layoutConfirmPassInput.error = "Min length 8"
                }
                inputDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = curPass && nPass && cPass
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        inputDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            fields.visibility = View.GONE
            loading.visibility = View.VISIBLE
//            updatePassword(
//                currentPass.text.trim().toString(),
//                newPass.text.trim().toString(),
//                fields,
//                loading,
//                inputDialog,
//                layoutCurrentPassInput
//            )

        }
    }

//    private fun updatePassword(
//        currentPass: String, newPass: String, fields: LinearLayout, loading: ProgressBar,
//        inputDialog: AlertDialog, layoutCurrentPassInput: TextInputLayout
//    ) {
//
//        InternetCheck { internet ->
//
//            if (internet) {
//                userService().updateProfile(userId, currentPass, newPass).enqueue(object : Callback<APIResponseMsg?> {
//                    override fun onResponse(call: Call<APIResponseMsg?>?, response: Response<APIResponseMsg?>?) {
//
//                        if (response != null && response.isSuccessful) {
//
//                            if (response.body()?.success == 1) {
//                                when (response.body()?.code) {
//                                    APICode.NOT_VALID -> {
//                                        fields.visibility = View.VISIBLE
//                                        loading.visibility = View.GONE
//                                        toastLong("${response.body()?.message}")
//                                        layoutCurrentPassInput.error = response.body()?.message
//                                    }
//                                    APICode.OK -> {
//                                        inputDialog.dismiss()
//                                        toastShort("Password updated.")
//                                        Prefs(this@UserProfileActivity).put(Keys.PASSWORD, newPass.encrypt())
//                                    }
//                                }
//                            } else {
//                                fields.visibility = View.VISIBLE
//                                loading.visibility = View.GONE
//                                toastLong("Cannot update password!")
//                            }
//
//                        } else {
//                            fields.visibility = View.VISIBLE
//                            loading.visibility = View.GONE
//                            toastLong("Invalid response from Server!")
//                        }
//
//                    }
//
//                    override fun onFailure(call: Call<APIResponseMsg?>?, t: Throwable?) {
//                        fields.visibility = View.VISIBLE
//                        loading.visibility = View.GONE
//                        toastLong("Failed to update password!")
//                    }
//                })
//            } else {
//                noInternetFragment()
//            }
//
//        }
//
//    }

    private fun verifyStorageCameraPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, permissions[2]) == PackageManager.PERMISSION_GRANTED
        ) {
            selectImageDialog()
            permissionsGranted = true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImageDialog()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun selectImageDialog() {
        startActivityForResult(CropImage.activity().getIntent(this), IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.uri
                profile_image.setImageURI(imageUri)
                imageSelected = true
                info("$imageUri")
//                uploadProfileImage()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                error("${error.message}")
            }
        }

    }

//    private fun uploadProfileImage() {
//
//        InternetCheck { internet ->
//
//            if (internet) {
//
//                if (imageUri != null) {
//                    val profileImagePath = getImagePath(imageUri!!, this@UserProfileActivity)
//                    val profileImageFile = File(profileImagePath)
//                    val profileFile = RequestBody.create(MediaType.parse("image/*"), profileImageFile)
//                    val profileImage =
//                        MultipartBody.Part.createFormData("profile_image", profileImageFile.name, profileFile)
//
//                    userService().updateProfileImage(getBodyPart(userId), profileImage)
//                        .enqueue(object : Callback<APIResponseMsg?> {
//                            override fun onResponse(call: Call<APIResponseMsg?>?, response: Response<APIResponseMsg?>?) {
//
//                                if (response != null && response.isSuccessful && response.body()?.success == 1 && response.body()?.code == 1) {
//                                    imageUpdated = true
//                                    toastShort("Profile image updated.")
//
//                                } else {
//                                    toastShort("Cannot upload profile image.")
//
//                                }
//
//                            }
//
//                            override fun onFailure(call: Call<APIResponseMsg?>?, t: Throwable?) {
//                                toastShort("Cannot upload profile image.")
//                            }
//                        })
//
//
//                }
//
//            } else {
//                noInternetFragment()
//            }
//
//        }
//
//    }

}
