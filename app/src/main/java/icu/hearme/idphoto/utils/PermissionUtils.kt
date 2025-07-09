package icu.hearme.idphoto.utils

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build

// 相册权限适配
fun requestPermissions(onResult: (Array<String>) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        onResult(
            arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
        )
    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
        onResult(arrayOf(READ_MEDIA_IMAGES))
    } else {
        onResult(arrayOf(READ_EXTERNAL_STORAGE))
    }
}