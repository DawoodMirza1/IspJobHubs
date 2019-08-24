package com.crossbugJOBHUB.retrofit.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("id") var id: Long = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("email") var email: String = "",
    @SerializedName("status") var status: String = ""
) : Serializable