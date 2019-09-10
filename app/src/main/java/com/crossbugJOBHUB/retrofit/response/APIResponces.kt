package com.crossbugJOBHUB.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class APIResponseMsg(
    @SerializedName("success") val success: Int = 0,
    @SerializedName("code") val code: Int = 0,
    @SerializedName("message") val message: String = ""
) : Serializable

class APIResponces<T : Serializable>(
    @SerializedName("success") val success: Int = 0,
    @SerializedName("code") val code: Int = 0,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: T
) : Serializable

class APIResponcesList<T : Serializable>(
    @SerializedName("success") val success: Int = 0,
    @SerializedName("code") val code: Int = 0,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: MutableList<T>
) : Serializable
