package com.crossbugJOBHUB.commons

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MediaType
import okhttp3.RequestBody


fun getBodyPart(value: String): RequestBody {
    return RequestBody.create(MediaType.parse("text/plain"), value)
}

fun getBodyPart(value: Long): RequestBody {
    return RequestBody.create(MediaType.parse("text/plain"), value.toString())
}

fun getBodyPart(value: Int): RequestBody {
    return RequestBody.create(MediaType.parse("text/plain"), value.toString())
}

fun getImagePath(uri: Uri, context: Context): String {
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
    if(cursor != null) {
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        cursor.close()
        return cursor.getString(columnIndex)
    }
    return uri.path ?: ""
}