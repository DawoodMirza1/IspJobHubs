package com.crossbugJOBHUB.retrofit.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("id") var id: Long = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("father_name") var fatherName: String = "",
    @SerializedName("username") var username: String = "",
    @SerializedName("email") var email: String = "",
    @SerializedName("gander") var gander: Int = 0,
    @SerializedName("status") var status: String = "",
    @SerializedName("matric") var matric: String = "",
    @SerializedName("inter") var inter: String = "",
    @SerializedName("age") var age: Int = 0,
    @SerializedName("cgpa") var cgpa: Double = 0.0,
    @SerializedName("image") var imageUrl: String = "",
    @SerializedName("address") var address: String = ""
) : Serializable

data class Job(@SerializedName("id") var id: Long = 0,
               @SerializedName("title") var title: String = "",
               @SerializedName("description") var description: String = ""
               ): Serializable