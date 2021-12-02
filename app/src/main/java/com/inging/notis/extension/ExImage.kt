package com.inging.notis.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by annasu on 2021/04/29.
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

fun BitmapDrawable.saveFile(context: Context, dir: String): String {
    return bitmap.saveFile(context, dir)
}

/**
 * 이미지 변환
 */
fun Bitmap.saveFile(context: Context, dir: String): String {
    val storage = "${context.cacheDir}/$dir"
    val imgFile = File(storage, "temp")
    var result = ""
    try {
        File(storage).mkdirs()
        imgFile.createNewFile()
        val out = FileOutputStream(imgFile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            compress(Bitmap.CompressFormat.WEBP_LOSSY, 75, out)
        } else {
            compress(Bitmap.CompressFormat.WEBP, 75, out)
//            compress(Bitmap.CompressFormat.PNG, 70, out)
        }
        out.close()

        // 파일명을 파일 사이즈로 하고 체크
        val fileSize = imgFile.length().toString()
        val newFile = File(storage, fileSize).apply {
            delete()
        }
        imgFile.renameTo(newFile)
        result = "$dir/$fileSize"
    } catch (e: FileNotFoundException) {
        Log.e("saveBitmapToJpg", "FileNotFoundException : " + e.message)
    } catch (e: IOException) {
        Log.e("saveBitmapToJpg", "IOException : " + e.message)
    }
    Log.d("imgPath", context.cacheDir.absolutePath + "/" + dir)
    return result
}

// drawable -> bitmap
fun Drawable.getBitmap(): Bitmap? {
    val bmp = Bitmap.createBitmap(
        intrinsicWidth,
        intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bmp)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bmp
}