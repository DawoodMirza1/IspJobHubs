package com.crossbugJOBHUB.retrofit.interfaces

import com.crossbugJOBHUB.retrofit.config.RetrofitAPI
import com.crossbugJOBHUB.retrofit.models.User
import com.crossbugJOBHUB.retrofit.response.APIResponces
import com.crossbugJOBHUB.retrofit.response.APIResponseMsg
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface UserService {

    @GET("users/get_users.php")
    fun getUsers(): Call<MutableList<User>>

    @GET("users/get_user.php")
    fun getUser(@Query("uid") userId: Int = 0): Call<MutableList<User>>

    @FormUrlEncoded
    @POST("users/authentication.php")
    fun authenticate(@Field("username") username: String,
                     @Field("password") password: String): Call<APIResponces<User>>

    @Multipart
    @POST("users/save_user.php")
    fun saveUser(@Part("user") user: User): Call<APIResponces<User>>

    @Multipart
    @POST("users/update_profile_image.php")
    fun updateProfileImage(@Part("id") id: RequestBody,
                           @Part profileImage: MultipartBody.Part): Call<APIResponseMsg>

    @FormUrlEncoded
    @POST("users/update_profile.php")
    fun updateProfile(@Field("id") userId: Long,
                      @Field("old_password") oldPassword: String,
                      @Field("new_password") newPassword: String,
                      @Field("name") name: String = ""): Call<APIResponseMsg>

    @GET("users/delete.php")
    fun delete(@Query("uid") userId: Long = 0L): Call<APIResponces<User>>
}

fun userService(): UserService = RetrofitAPI.getClient().create(UserService::class.java)