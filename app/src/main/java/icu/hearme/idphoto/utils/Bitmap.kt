package icu.hearme.idphoto.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

// 将图片旋转90°
fun Bitmap.rotate(a: Boolean): ImageBitmap{
    val matrix = Matrix().apply {
        postRotate(if (a) 90f else 0f)
    }
    // 关键点：交换宽高参数
    return Bitmap.createBitmap(
        this,
        0, 0,
        this.width, this.height,
        matrix, true
    ).asImageBitmap()
}