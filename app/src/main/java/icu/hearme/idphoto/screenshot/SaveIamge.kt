package icu.hearme.idphoto.screenshot

import android.Manifest
import icu.hearme.idphoto.R
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun SaveImageToCustomFolder(
    bitmap: Bitmap,
    folderName: String = "idPhotoImages",
    imageName: String = "image_${System.currentTimeMillis()}.jpg",
    tips: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 请求存储权限（同上）
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                saveToCustomFolderAdvanced(context, bitmap, folderName, imageName)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
        Button(onClick = {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }, contentPadding = PaddingValues(8.dp)) {
            Image(painterResource(R.drawable.ic_save_pic), contentDescription = null)
            tips?.let { Text(it) }
        }
    }
}

suspend fun saveToCustomFolderAdvanced(
    context: Context,
    bitmap: Bitmap,
    folderName: String,
    imageName: String
): Uri? = withContext(Dispatchers.IO) {
    val resolver = context.contentResolver

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+ 使用MediaStore API
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$folderName")
        }

        val imageUri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "图片已保存到: $folderName/$imageName", Toast.LENGTH_LONG).show()
        }
        return@withContext imageUri
    } else {
        // 旧版本使用传统方法
        val imagesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        val customFolder = File(imagesDir, folderName)

        if (!customFolder.exists()) {
            customFolder.mkdirs()
        }

        val imageFile = File(customFolder, imageName)
        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "图片已保存到: $folderName/$imageName", Toast.LENGTH_LONG).show()
        }
        // 返回文件URI
        return@withContext Uri.fromFile(imageFile)
    }
}