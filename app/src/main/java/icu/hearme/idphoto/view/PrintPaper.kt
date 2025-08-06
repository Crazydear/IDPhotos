package icu.hearme.idphoto.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import icu.hearme.idphoto.enums.PicSizeType
import icu.hearme.idphoto.utils.rotate


// 1寸照片 5寸相纸
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrintingPaper3R8(
    bitmap: ImageBitmap,
    inputSizeType: PicSizeType = PicSizeType.OneInch,
    modifier: Modifier = Modifier
) {
    Surface(modifier.commonBg(PicSizeType.ThreeInch.getAspectRatio()),
        color = Color.White
    ) {
        FlowRow(maxItemsInEachRow = 2, modifier = Modifier.fillMaxSize(),
            horizontalArrangement =  Arrangement.SpaceEvenly, verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0..7){
                Image(painter = BitmapPainter(bitmap.asAndroidBitmap().rotate(true)),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(inputSizeType.height.toFloat() / PicSizeType.ThreeInch.width)
                        .aspectRatio(1/inputSizeType.getAspectRatio())
                        .commonBorder()
                    )
            }
        }
    }
}

// 1寸2寸相片 5寸相纸
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrintingPaper3R6(
    bitmap: ImageBitmap,
    inputSizeType: PicSizeType = PicSizeType.OneInch,
    modifier: Modifier = Modifier
) {
    Box(modifier.commonBg(PicSizeType.ThreeInch.getAspectRatio()),
        contentAlignment = Alignment.Center
    ) {
        FlowRow(maxItemsInEachRow = 2,
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement =  Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 1..4){
                Image(painter = BitmapPainter(bitmap.asAndroidBitmap().rotate(true)),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(inputSizeType.height.toFloat() / PicSizeType.ThreeInch.width)
                        .aspectRatio(1/ PicSizeType.OneInch.getAspectRatio())
                        .commonBorder()
                )
            }
            for (i in 1..2){
                Image(painter = BitmapPainter(bitmap),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(PicSizeType.TwoInch.width.toFloat() / PicSizeType.ThreeInch.width)
                        .commonBorder()
                )
            }
        }
    }
}

// 1寸照片 6寸相纸
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrintingPaper4R8(
    bitmap: ImageBitmap,
    inputSizeType: PicSizeType = PicSizeType.OneInch,
    modifier: Modifier = Modifier
) {
    Box(modifier.commonBg(PicSizeType.FourInch.getAspectRatio()),
        contentAlignment = Alignment.Center
    ) {
        FlowRow(maxItemsInEachRow = 2, modifier = Modifier.fillMaxSize(),
            horizontalArrangement =  Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0..7){
                Image(painter = BitmapPainter(bitmap.asAndroidBitmap().rotate(true)),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(inputSizeType.height.toFloat() / PicSizeType.FourInch.width)
                        .aspectRatio(1/inputSizeType.getAspectRatio())
                        .commonBorder()
                    )
            }
        }
    }
}

// 1寸2寸相片 6寸相纸
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrintingPaper4R81(
    bitmap: ImageBitmap,
    inputSizeType: PicSizeType = PicSizeType.OneInch,
    modifier: Modifier = Modifier
) {
    Box(modifier.commonBg(PicSizeType.FourInch),
        contentAlignment = Alignment.Center
    ) {
        FlowColumn(maxItemsInEachColumn = 3,
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement =  Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.SpaceEvenly //spacedBy(0.5.dp, Alignment.CenterVertically)
        ) {
            for (i in 0..5){
                Image(painter = BitmapPainter(bitmap), contentDescription = null,
                    modifier = Modifier.fillMaxWidth(inputSizeType.width.toFloat() / PicSizeType.FourInch.width)
                        .commonBorder()
                )
            }
            for (i in 0..1){
                Image(painter = BitmapPainter(bitmap), contentDescription = null,
                    modifier = Modifier.fillMaxWidth(PicSizeType.TwoInch.width.toFloat() / PicSizeType.FourInch.width)
                        .fillMaxHeight(PicSizeType.TwoInch.height.toFloat() / PicSizeType.FourInch.height)
                        .commonBorder()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrintingPaper4R11(
    bitmap: ImageBitmap,
    inputSizeType: PicSizeType = PicSizeType.OneInch,
    modifier: Modifier = Modifier
) {
    Box(modifier.commonBg(PicSizeType.FourInch),
        contentAlignment = Alignment.Center
    ) {
        FlowColumn(maxItemsInEachColumn = 4,
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement =  Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.SpaceEvenly //spacedBy(0.5.dp, Alignment.CenterVertically)
        ) {
            for (i in 0..7){
                Image(painter = BitmapPainter(bitmap), contentDescription = null,
                    modifier = Modifier.fillMaxWidth(inputSizeType.width.toFloat() / PicSizeType.FourInch.width)
                        .commonBorder()
                )
            }
            for (i in 0..2){
                Image(painter = BitmapPainter(bitmap), contentDescription = null,
                    modifier = Modifier.fillMaxWidth(PicSizeType.TwoInch.width.toFloat() / PicSizeType.FourInch.width)
                        .fillMaxHeight(PicSizeType.TwoInch.height.toFloat() / PicSizeType.FourInch.height)
                        .commonBorder()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomPrintPaper(
    bitmap: ImageBitmap,
    inputSizeType: PicSizeType = PicSizeType.OneInch,
    pageType: PicSizeType,
    modifier: Modifier = Modifier
){
    val maxItemsInEachColumn = pageType.width / inputSizeType.width
    val maxLines = pageType.height / inputSizeType.height

    Box(modifier.commonBg(pageType),
        contentAlignment = Alignment.Center
    ) {
        FlowColumn(maxItemsInEachColumn = maxItemsInEachColumn,
            maxLines = maxLines,
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement =  Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.SpaceEvenly // spacedBy(1.dp, Alignment.CenterVertically)
        ) {
            for (i in 0 until maxItemsInEachColumn * maxLines){
                Image(
                    painter = BitmapPainter(bitmap),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(inputSizeType.width.toFloat() / pageType.width)
                        .commonBorder()
                )
            }
        }
    }
}

private fun Modifier.commonBg(aspectRatio: Float) = this.fillMaxWidth().background(Color.White).aspectRatio(aspectRatio)

private fun Modifier.commonBg(picSizeType: PicSizeType) = this.size(picSizeType.getDpSize()).background(Color.White)
private fun Modifier.commonBorder() = this.border(1.dp,Color.LightGray)
