package icu.hearme.idphoto.page

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationScene
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting
import icu.hearme.idphoto.R
import icu.hearme.idphoto.model.IDViewModel
import icu.hearme.idphoto.utils.createMediaStoreUri
import icu.hearme.idphoto.utils.getLatestPhoto
import icu.hearme.idphoto.utils.requestPermissions
import icu.hearme.idphoto.view.Copyright
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun MainPage(viewModel: IDViewModel = viewModel(), modifier: Modifier) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmapTemp by remember { mutableStateOf<Bitmap?>(null) }
    val uri = remember { createMediaStoreUri(context) }
    var lauchType by remember { mutableStateOf(0) }
    var showProgress by remember { mutableStateOf(false) }
    // 华为机器学习去背景
    // 方式二：使用自定义参数MLImageSegmentationSetting配置图像分割检测器。
    val setting = MLImageSegmentationSetting.Factory()
        .setExact(true)         // 设置分割精细模式，true为精细分割模式，false为速度优先分割模式。
        .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)   // 设置分割模式为人像分割。
        .setScene(MLImageSegmentationScene.FOREGROUND_ONLY)     // 设置返回结果种类。
        .create()
    val analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(setting)

    LaunchedEffect(imageUri) {
        if (lauchType == 1) {
            imageUri?.let { uri ->
                withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            bitmapTemp = BitmapFactory.decodeStream(stream)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        }
    }

    LaunchedEffect(bitmapTemp) {
        bitmapTemp?.let {
            showProgress = true
        }
        delay(500)
        bitmapTemp?.let {
            viewModel.setReginBitmap(it)
            if(!it.isRecycled) {
                val frame = MLFrame.fromBitmap(it)
                val segmentations = analyzer!!.analyseFrame(frame)
                viewModel.setRemoveBgBitmap(segmentations.get(0).foreground)
                showProgress = false
                viewModel.navController.value?.navigate("changebg")
            }
        }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        showProgress = true
    }

    // 相机启动器
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // 直接从MediaStore读取最新照片
            bitmapTemp = getLatestPhoto(context)
            showProgress = true
        }
    }

    // 检查并请求读取外部存储权限
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        if (isGranted.all { it.value }) {
            when (lauchType){
                1 -> launcher.launch("image/*")
                2 -> cameraLauncher.launch(uri)
            }
        } else {
            val tip = when (lauchType){
                1 -> "需要权限才能访问相册"
                2 -> "需要拍照权限才能访问使用相机"
                else -> ""
            }
            Toast.makeText(context, tip, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment= Alignment.Center) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(modifier = Modifier.fillMaxWidth(0.5f).background(Color.White)
                .clickable(onClick = {
                    lauchType = 2
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                }),
                colors = CardColors(Color.Transparent, Color.Black, Color.Black, Color.Black,)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = null,
                        Modifier.size(60.dp)
                    )
                    Text("一键拍照制作", style = TextStyle.Default.copy(fontSize = 25.sp, fontWeight = FontWeight.Bold))
                }
            }

            OutlinedCard(modifier = Modifier.fillMaxWidth(0.5f).background(Color.White)
                .clickable(onClick = {
                    lauchType = 1
                    requestPermissions { reslut -> permissionLauncher.launch(reslut) }
                }),
                colors = CardColors(Color.White,Color.Black,Color.Black,Color.Black, )
            ) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.ic_pic),
                        contentDescription = null,
                        Modifier.size(60.dp)
                    )
                    Text("本地照片制作", style = TextStyle.Default.copy(fontSize = 25.sp, fontWeight = FontWeight.Bold))
                }
            }

            OutlinedCard(modifier = Modifier.fillMaxWidth(0.5f).background(Color.White)
                .clickable(onClick = {
                    viewModel.navController.value?.navigate("paperprint")
                }),
                colors = CardColors(Color.White,Color.Black,Color.Black,Color.Black, )
            ) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.ic_test_paper),
                        contentDescription = null,
                        Modifier.size(60.dp)
                    )
                    Text("试卷打印", style = TextStyle.Default.copy(fontSize = 25.sp, fontWeight = FontWeight.Bold))
                }
            }
        }
        Copyright(
            modifier = Modifier.offset(y = with(LocalDensity.current) { (-20).dp })
            .align(Alignment.BottomCenter)
        )
        if (showProgress){
            CircularProgressIndicator(
                modifier = Modifier.size(30.dp)
                    .offset(y = with(LocalDensity.current){ (-40).dp })
                    .align(Alignment.BottomCenter)
            )
        }
    }
}