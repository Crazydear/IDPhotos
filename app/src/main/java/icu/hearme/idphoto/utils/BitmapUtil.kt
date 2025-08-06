package icu.hearme.idphoto.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.indices
import kotlin.collections.mapNotNull
import kotlin.io.use
import kotlin.ranges.step

// 试卷打印模块
fun createSinglePage(bitmap: Bitmap): Bitmap {
    // 创建一张足够大的画布，将图片居中显示
    val width = (bitmap.width)// * 1.5).toInt()
    val height = (bitmap.height)// * 1.5).toInt()

    val page = createBitmap(width, height)
    val canvas = Canvas(page)

    // 绘制背景
    canvas.drawColor(Color.White.value.toInt())

    // 计算居中位置
    val left = (width - bitmap.width) / 2
    val top = (height - bitmap.height) / 2

    // 绘制图片
    canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), null)

    return page
}

suspend fun prepareImagesForPrinting(context: Context, uris: List<Uri>): List<Bitmap> {
    return withContext(Dispatchers.IO) {
        val bitmaps = uris.mapNotNull { uri ->
            loadBitmap(context, uri)
        }

        val result = mutableListOf<Bitmap>()

        // 每两张图片组合成一页
        for (i in bitmaps.indices step 2) {
            val bitmap1 = bitmaps[i]
            val bitmap2 = if (i + 1 < bitmaps.size) bitmaps[i + 1] else null

            val combined = if (bitmap2 != null) {
                bitmap1.combine(bitmap2)
                // combineTwoImages(bitmap1, bitmap2)
            } else {
                // 如果只有一张图片，居中显示
                createSinglePage(bitmap1)
            }
            result.add(combined)
        }

        result
    }
}

fun loadBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}