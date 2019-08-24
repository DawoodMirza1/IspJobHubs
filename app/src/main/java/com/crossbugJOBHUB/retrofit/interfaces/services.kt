package com.crossbugJOBHUB.retrofit.interfaces

import com.crossbugJOBHUB.retrofit.config.RetrofitAPI
import com.crossbugJOBHUB.retrofit.models.User
import com.crossbugJOBHUB.retrofit.response.APIResponces
import retrofit2.Call
import retrofit2.http.*


interface UserService {

    @GET("users/get_users.php")
    fun getUsers(): Call<MutableList<User>>

    @GET("users/get_user.php")
    fun getAreas(@Query("uid") userId: Int = 0): Call<MutableList<User>>

    @Multipart
    @POST("users/save_user.php")
    fun saveUser(@Part("user") user: User): Call<APIResponces<User>>

    @GET("users/delete.php")
    fun delete(@Query("uid") userId: Long = 0L): Call<APIResponces<User>>
}

fun userService(): UserService = RetrofitAPI.getClient().create(UserService::class.java)