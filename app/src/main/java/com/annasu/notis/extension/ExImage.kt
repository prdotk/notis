package com.annasu.notis.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by datasaver on 2021/04/29.
 */

suspend fun String.loadBitmap(context: Context): Bitmap? {
    return try {
        withContext(Dispatchers.Default) {
            val imageFile = File(context.cacheDir, this@loadBitmap)
            var bitmap: Bitmap? = null
            if (imageFile.exists()) {
                bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            }
            bitmap
        }
    } catch (e: Exception) {
        null
    }
}

fun BitmapDrawable.saveIcon(context: Context, name: String): String {
    if (!bitmap.saveFile(context, name)) {
        return ""
    }
    return name
}

/**
 * 저장된 파일이 있는지 체크
 */
fun Bitmap.saveIcon(context: Context, name: String): String {
//    val name = hashCode().toString()
//    val storage = context.cacheDir
//    val imageFile = File(storage, "$name")
//    if (!imageFile.exists()) {
    if (!saveFile(context, name)) {
        return ""
    }
//    }
    return name
}

/**
 * 이미지 변환
 */
fun Bitmap.saveFile(context: Context, name: String): Boolean {
    val storage = context.cacheDir
    val imgFile = File(storage, name)
    var result = false
    try {
        imgFile.createNewFile()
        val out = FileOutputStream(imgFile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, out)
        } else {
            compress(Bitmap.CompressFormat.PNG, 70, out)
        }
        out.close()
        result = true
    } catch (e: FileNotFoundException) {
        Log.e("saveBitmapToJpg", "FileNotFoundException : " + e.message)
    } catch (e: IOException) {
        Log.e("saveBitmapToJpg", "IOException : " + e.message)
    }
    Log.d("imgPath", context.cacheDir.absolutePath + "/" + name)
    return result
}