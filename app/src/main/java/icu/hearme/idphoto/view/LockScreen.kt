package icu.hearme.idphoto.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun LockScreenOrientation(lockedPortrait: Boolean = true) {
    val context = LocalContext.current
    val currentActivity = context.findActivity()

    DisposableEffect(lockedPortrait) {
        val activity = currentActivity ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation

        if (lockedPortrait) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        onDispose {
            // 可选：恢复原始方向
            activity.requestedOrientation = originalOrientation
        }
    }
}

// 扩展函数：获取宿主 Activity
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}