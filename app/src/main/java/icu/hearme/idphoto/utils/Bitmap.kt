package icu.hearme.idphoto.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap

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

fun Bitmap.combine(bitmap: Bitmap): Bitmap {
    // 确定组合后的尺寸（取两张图片的最大高度）
    val height = kotlin.comparisons.maxOf(this.height, bitmap.height)
    val width = this.width + bitmap.width

    val combined = createBitmap(width, height)
    val canvas = Canvas(combined)

    // 绘制背景
    canvas.drawColor(Color.White.value.toInt())

    // 计算图片位置（居中显示）
    val left1 = 0
    val top1 = (height - this.height) / 2

    val left2 = this.width
    val top2 = (height - bitmap.height) / 2

    // 绘制图片
    canvas.drawBitmap(this, left1.toFloat(), top1.toFloat(), null)
    canvas.drawBitmap(bitmap, left2.toFloat(), top2.toFloat(), null)

    return combined
}