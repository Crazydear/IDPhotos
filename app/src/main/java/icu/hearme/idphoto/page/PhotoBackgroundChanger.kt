package icu.hearme.idphoto.page

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import icu.hearme.idphoto.R
import icu.hearme.idphoto.model.IDViewModel
import icu.hearme.idphoto.screenshot.ImageResult
import icu.hearme.idphoto.screenshot.ScreenshotBox
import icu.hearme.idphoto.screenshot.rememberScreenshotState

@Composable
fun PhotoBackgroundChanger(viewModel: IDViewModel = viewModel()) {
    val regionBitmap by viewModel.rBitmap.collectAsState()
    val bitmap by viewModel.removebgBitmap.collectAsState()
    val defalt = painterResource(R.drawable.defaultpic)
    var bgColor by remember { mutableStateOf(Color.White) }
    val screenshotState = rememberScreenshotState()
    val buttonColor = listOf(Color.White, Color(0,191,243),Color.Red, Color.DarkGray)
    var regBitmap by remember { mutableStateOf(false) }

    val painter by produceState(initialValue = defalt, bitmap, regBitmap) {
        if (regBitmap){
            regionBitmap?.let {
                value = BitmapPainter(it)
            }

        } else {
            bitmap?.let {
                value = BitmapPainter(it)
            }
        }
    }
    val imageResult: ImageResult = screenshotState.imageState.value

    LaunchedEffect(key1 = imageResult){
        if (imageResult is ImageResult.Success && !imageResult.data.isRecycled){
            val copiedBitmap = imageResult.data.copy(imageResult.data.config!!, true)
            viewModel.setOptBitmap(copiedBitmap, bgColor)
            viewModel.navController.value?.navigate("crop")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f), contentAlignment = Alignment.Center){
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(bgColor)
            }
            SubcomposeLayout{
                    constraints ->
                // 先测量Image
                val imagePlaceable = subcompose("image") {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                }[0].measure(constraints)

                // 再用Image的尺寸测量ScreenshotBox
                val screenshotPlaceable = subcompose("screenshot") {
                    ScreenshotBox(
                        screenshotState = screenshotState,
                        modifier = Modifier.size(imagePlaceable.width.toDp(), imagePlaceable.height.toDp())){}
                }[0].measure(Constraints.fixed(imagePlaceable.width, imagePlaceable.height))

                layout(imagePlaceable.width, imagePlaceable.height) {
                    imagePlaceable.place(0, 0)
                    screenshotPlaceable.place(0, 0)
                }
            }
        }
        Spacer(modifier = Modifier.height(with(LocalDensity){ 10.dp }))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = with(LocalDensity) { 20.dp })) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceEvenly) {
                buttonColor.forEachIndexed { index, color ->
                    FilledIconButton(onClick = {
                        bgColor = color
                        if (index == buttonColor.lastIndex){ regBitmap = true }
                        else { regBitmap = false }
                    }, colors = IconButtonDefaults.filledIconButtonColors(color)) {
                        Canvas(modifier = Modifier) {
                            drawArc(color, 0f,4f,true)
                            drawArc(Color.DarkGray,0f,4f,true, style = Stroke(2f))
                        }
                        if (index == buttonColor.lastIndex && bgColor != color){
                            Icon(painter = painterResource(R.drawable.ic_nochange), contentDescription = null)
                        }
                        if (bgColor == color){
                            Icon(Icons.Default.Done, contentDescription = null)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(with(LocalDensity){ 10.dp }))
            Button(onClick = {
                screenshotState.capture()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("下一步")
            }
        }
    }
}