package icu.hearme.idphoto.page

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import icu.hearme.idphoto.screenshot.ScreenshotBox
import icu.hearme.idphoto.screenshot.rememberScreenshotState
import icu.hearme.idphoto.R
import icu.hearme.idphoto.adapter.BitmapPrinter
import icu.hearme.idphoto.enums.PicSizeType
import icu.hearme.idphoto.model.IDViewModel
import icu.hearme.idphoto.screenshot.ImageResult
import icu.hearme.idphoto.screenshot.SaveImageToCustomFolder
import icu.hearme.idphoto.view.CustomPrintPaper
import icu.hearme.idphoto.view.PrintingPaper3R6
import icu.hearme.idphoto.view.PrintingPaper3R8
import icu.hearme.idphoto.view.PrintingPaper4R11
import icu.hearme.idphoto.view.PrintingPaper4R8
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoPreviewPage(viewModel: IDViewModel = viewModel()) {
    val context = LocalContext.current
    val optBitmap by viewModel.optBitmap.collectAsState()
    val picType by viewModel.picSizeType.collectAsState()
    val screenshotState = rememberScreenshotState()
    val screenshotState2 = rememberScreenshotState()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        pageCount = { 2 }
    )
    val titles = stringArrayResource(R.array.pre_title)
    var showExButton by remember { mutableStateOf<Boolean>(false) }
    val defaultPic = ImageBitmap.imageResource(R.drawable.defaultpic)


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(40.dp)
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(title,  style = LocalTextStyle.current.copy(
                                fontSize = if (pagerState.currentPage == index) 25.sp else 20.sp,
                                textDecoration = if (pagerState.currentPage == index) TextDecoration.Underline else null,
                                color = if (pagerState.currentPage == index) Color.Blue else Color.DarkGray
                            ))
                               },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                                showExButton = false
                            }
                        })
                }
            }

            HorizontalPager(state = pagerState,
                modifier = Modifier.fillMaxSize()
                .weight(1f),
                verticalAlignment = Alignment.Top) { page ->
                when (page) {
                    0 -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            Text("尺寸：" + picType.value,
                                modifier = Modifier.align(Alignment.TopCenter)
                                    .offset(y = with(LocalDensity.current){ 15.dp })
                            )
                            ScreenshotBox(screenshotState = screenshotState, modifier = Modifier.wrapContentSize()){
                                Image(painter = optBitmap?.let { BitmapPainter(it) } ?:
                                painterResource(R.drawable.defaultpic), contentDescription = null, contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.fillMaxWidth(0.6f).aspectRatio(picType.getAspectRatio())
                                )
                            }
                        }
                    }
                    1 -> {
                        val printSize by viewModel.printSizeType.collectAsState()
                        var screenMode by rememberSaveable { mutableStateOf(0) }
                        LaunchedEffect(printSize) {
                            showExButton = false
                            screenMode = 0
                        }
                        LaunchedEffect(screenMode) { showExButton = false }

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            FlowRow(modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)) {
                                TextButton(onClick = { screenMode = 0 }) {
                                    Text(stringResource(R.string.layout_auto))
                                }
                                when (printSize){
                                    PicSizeType.ThreeInch -> {
                                        if (picType == PicSizeType.OneInch) {
                                            TextButton(onClick = { screenMode = 1 }) {
                                                Text(stringResource(R.string.layout_double))
                                            }
                                        }
                                        TextButton(onClick = { screenMode = 2 }) {
                                            Text(stringResource(R.string.layout_one))
                                        }

                                    }
                                    PicSizeType.FourInch -> {
                                        if (picType == PicSizeType.OneInch) {
                                            TextButton(onClick = { screenMode = 3 }) {
                                                Text(stringResource(R.string.layout_double))
                                            }
                                        }
                                        TextButton(onClick = { screenMode = 4 }) {
                                            Text(stringResource(R.string.layout_one))
                                        }
                                    }
                                    else -> {}
                                }
                            }

                            AdaptiveSizeBox(modifier = Modifier.align(Alignment.Center).wrapContentSize()) {
                                ScreenshotBox(screenshotState = screenshotState2, modifier = Modifier.wrapContentSize()){

                                    when (screenMode) {
                                        0 -> {
                                            CustomPrintPaper(
                                                optBitmap ?: defaultPic,
                                                picType, printSize,
                                                modifier = Modifier.fillMaxWidth(0.75f)
                                            )
                                        }

                                        1 -> {
                                            PrintingPaper3R6(
                                                optBitmap ?: defaultPic,
                                                picType,
                                                modifier = Modifier.fillMaxWidth(0.75f)
                                            )
                                        }

                                        2 -> {
                                            PrintingPaper3R8(
                                                optBitmap ?: defaultPic,
                                                picType,
                                                modifier = Modifier.fillMaxWidth(0.75f)
                                            )
                                        }

                                        3 -> {
                                            PrintingPaper4R11(
                                                optBitmap ?: defaultPic,
                                                picType,
                                                modifier = Modifier.fillMaxWidth(0.75f)
                                            )
                                        }

                                        4 -> {
                                            PrintingPaper4R8(
                                                optBitmap ?: defaultPic,
                                                picType,
                                                modifier = Modifier.fillMaxWidth(0.75f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.75f)
                .align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Button(onClick = {
                    when (pagerState.currentPage) {
                        0 -> { screenshotState.capture() }
                        1 -> { screenshotState2.capture() }
                    }
                    showExButton = true
                }) {
                    Text(stringResource(R.string.complete))
                }
                if (showExButton) {
                    when (pagerState.currentPage) {
                        0 -> {
                            val imageResult: ImageResult = screenshotState.imageState.value
                            if (imageResult is ImageResult.Success && !imageResult.data.isRecycled) {
                                val copiedBitmap = imageResult.data.copy(imageResult.data.config!!, true)
                                SaveImageToCustomFolder(copiedBitmap, tips = stringResource(R.string.save_to_album))
                            }
                        }

                        1 -> {
                            val imageResult: ImageResult = screenshotState2.imageState.value
                            if (imageResult is ImageResult.Success && !imageResult.data.isRecycled) {
                                val copiedBitmap = imageResult.data.copy(imageResult.data.config!!, true)
                                val jobName = stringResource(R.string.photo_print)
                                Button(onClick = {
                                    BitmapPrinter.printBitmap(context, copiedBitmap, jobName)
                                }, modifier = Modifier.padding(start = 10.dp)) {
                                    Text(stringResource(R.string.print))
                                }
                                SaveImageToCustomFolder(copiedBitmap, tips = stringResource(R.string.save_to_album))
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AdaptiveSizeBox(
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.() -> Unit)
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // 判断父容器是否"横宽"（宽度 > 高度）
        val isWide = maxWidth > maxHeight

        Box(
            modifier = if (isWide) {
                Modifier.fillMaxWidth(0.75f) // 竖屏模式：高度限制为75%
            } else {
                Modifier.fillMaxHeight(0.60f)  // 横屏模式：宽度限制为75%
            }.apply {
                align(Alignment.Center)
            },
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}