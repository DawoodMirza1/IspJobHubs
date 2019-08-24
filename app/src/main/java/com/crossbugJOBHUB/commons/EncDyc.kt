package com.crossbugJOBHUB.commons

import se.simbio.encryption.Encryption
import java.util.*


// String extension function
//**************************************************************************************************
fun String.encrypt() = if(this.isNotBlank()) EncDyc.encrypt(this) else ""
fun String.decrypt() =  if(this.isNotBlank()) EncDyc.decrypt(this) else ""


//**************************************************************************************************
object EncDyc {

    private const val key = "JUpUASgBUwF8iNm2o"
    private const val salt = "E173CaBF29332ED92"
    private var iv = ByteArray(16)
    private var encryption = Encryption.getDefault(key, salt, iv)

    fun encrypt(data: String) = encryption.encryptOrNull(data) ?: ""

    fun decrypt(encData: String) = encryption.decryptOrNull(encData) ?: ""

}

fun getSaltString(length: Int = 15): String {
    val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    val salt = StringBuilder()
    val rnd = Random()
    while (salt.length < length) {
        val index = ((rnd.nextFloat() * SALTCHARS.length) % SALTCHARS.length).toInt()
        salt.append(SALTCHARS[index])
    }
    return salt.toString()
}