package icu.hearme.idphoto.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import icu.hearme.idphoto.R
import icu.hearme.idphoto.enums.PicSizeType
import icu.hearme.idphoto.model.IDViewModel
import icu.hearme.idphoto.view.PhotoCropper

@Composable
fun PhotoCropperPage(viewModel: IDViewModel = viewModel()) {
    val bitmap by viewModel.optBitmap.collectAsState()
    val picType by viewModel.picSizeType.collectAsState()
    val bgColor by viewModel.bgColor.collectAsState()
    val defalt = painterResource(R.drawable.defaultpic)
    val titles = listOf<PicSizeType>(PicSizeType.OneInch, PicSizeType.TwoInch, PicSizeType.DriverLicense)
    var cropAspectRatio by remember { mutableStateOf<Float>(PicSizeType.OneInch.getAspectRatio()) }


    val painter by produceState(initialValue = defalt, bitmap) {
        bitmap?.let {
            value = BitmapPainter(it)
        }
    }

    bitmap?.let{
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            PhotoCropper(
                painter = painter,
                cropAspectRatio = cropAspectRatio,
                onCrop = { cropped ->
                    cropped?.let {
                        viewModel.setOptBitmap(cropped)
                        viewModel.navController.value?.navigate("preview")
                    }
                }
            )

            Row(modifier = Modifier
                .fillMaxWidth(0.8f)
                .offset(0.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                titles.forEachIndexed { index, _picType ->
                    if (bgColor != Color.White && index == 2) return@forEachIndexed
                    TextButton(
                        onClick = {
                            viewModel.setPicSizeType(_picType)
                            cropAspectRatio = _picType.getAspectRatio()
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(contentColor = if (picType == _picType) Color.Red else Color.Blue)
                    ) {
                        Text(_picType.value,
                            style = LocalTextStyle.current.copy(
                                fontSize = if (picType == _picType) 30.sp else 20.sp,
                                textDecoration = if (picType == _picType) TextDecoration.Underline else null,
                                drawStyle = if (picType == _picType) Fill else null
                            )
                        )
                    }
                }
            }
        }
    }
}
