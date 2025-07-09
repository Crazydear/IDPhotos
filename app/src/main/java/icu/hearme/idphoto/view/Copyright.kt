package icu.hearme.idphoto.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
fun Copyright(modifier: Modifier) {
    val uriHandler = LocalUriHandler.current
    val annotatedText = buildAnnotatedString {
        append("Copyright 2025 ")
        // 为可点击部分添加注解
        pushStringAnnotation(
            tag = "URL",
            annotation = "https://github.com/Crazydear/IDPhotos"
        )
        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
            append("Github@CrazyDear")
        }
        pop()
        append(" 版权所有")
    }

    // 创建文本布局结果
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(Unit) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layout ->
                // 获取点击位置的字符偏移量
                val offset = layout.getOffsetForPosition(pos)
                // 检查点击是否在注解范围内
                annotatedText.getStringAnnotations("URL", offset, offset)
                    .firstOrNull()
                    ?.let { uriHandler.openUri(it.item) }
            }
        }
    }

    Text(
        text = annotatedText,
        modifier = modifier.then(pressIndicator),
        onTextLayout = { layoutResult.value = it }
    )
}