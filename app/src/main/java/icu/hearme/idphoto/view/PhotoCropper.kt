package icu.hearme.idphoto.view

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import icu.hearme.idphoto.screenshot.ImageResult
import icu.hearme.idphoto.screenshot.ScreenshotBox
import icu.hearme.idphoto.screenshot.rememberScreenshotState
import icu.hearme.idphoto.R
import kotlin.math.roundToInt

/**
 * 照片裁剪组件（仅支持缩放和平移）
 * @param painter 要裁剪的图片
 * @param cropAspectRatio 裁剪框宽高比（默认1:1）
 * @param maxScale 最大缩放比例（默认5倍）
 * @param onCrop 裁剪完成回调
 */
@Composable
fun PhotoCropper(
    painter: Painter,
    cropAspectRatio: Float = 1f,
    maxScale: Float = 5f,
    onCrop: (Bitmap?) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var cropRect by remember { mutableStateOf(Rect.Zero) }
    val screenshotState = rememberScreenshotState()
    val imageResult: ImageResult = screenshotState.imageState.value

    LaunchedEffect(key1 = imageResult){
        if (imageResult is ImageResult.Success && !imageResult.data.isRecycled){
            val copiedBitmap = imageResult.data.copy(imageResult.data.config!!, true)
            onCrop(copiedBitmap)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 可缩放平移的图片区域
        Box(
            modifier = Modifier.fillMaxSize().clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, maxScale)
                        offset += pan
                    }
                }
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            )
        }

        // 裁剪遮罩层
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cropWidth = size.width * 0.75f
            val cropHeight = cropWidth / cropAspectRatio
            cropRect = Rect(
                left = (size.width - cropWidth) / 2,
                top = (size.height - cropHeight) / 2,
                right = (size.width + cropWidth) / 2,
                bottom = (size.height + cropHeight) / 2
            )
            val revealedPath = Path().apply {
                addRect(cropRect)
            }

            // 绘制挖空遮罩
            Path().apply {
                addRect(size.toRect())
                op(this, revealedPath, PathOperation.Difference)
            }.let { path ->
                drawPath(path, Color.Black.copy(alpha = 0.6f))
            }

            // 绘制裁剪框边框
            drawRect(
                color = Color.White,
                topLeft = cropRect.topLeft,
                size = cropRect.size,
                style = Stroke(2.dp.toPx())
            )
        }

        ScreenshotBox(
            modifier = Modifier
                .offset {
                    IntOffset(cropRect.left.roundToInt(), cropRect.top.roundToInt())
                }
                .size(
                    width = with(LocalDensity.current) { cropRect.width.toDp() },
                    height = with(LocalDensity.current) { cropRect.height.toDp() }
                ),
                screenshotState = screenshotState
            ){}

        Button(
            onClick = { screenshotState.capture() },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 15.dp)
        ) {
            Text(stringResource(R.string.next))
        }
    }
}