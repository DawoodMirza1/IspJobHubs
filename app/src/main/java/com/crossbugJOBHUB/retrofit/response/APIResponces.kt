package com.crossbugJOBHUB.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class APIResponces<T : Serializable>(
    @SerializedName("success") val success: Int = 0,
    @SerializedName("code") val code: Int = 0,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: T
) : Serializable