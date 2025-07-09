package icu.hearme.idphoto.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import icu.hearme.idphoto.enums.PicSizeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IDViewModel: ViewModel() {
    private var _regionBitmap = MutableStateFlow<ImageBitmap?>(null)
    private var _removbgBitmap = MutableStateFlow<ImageBitmap?>(null)
    private var _optBitmap = MutableStateFlow<ImageBitmap?>(null)
    private var _navController = MutableStateFlow<NavHostController?>(null)
    private var _picType = MutableStateFlow<PicSizeType>(PicSizeType.OneInch)
    private var _printType = MutableStateFlow<PicSizeType>(PicSizeType.FourInch)
    private var _bgColor = MutableStateFlow(Color.White)
    val rBitmap: StateFlow<ImageBitmap?>
        get() = _regionBitmap.asStateFlow()

    val removebgBitmap: StateFlow<ImageBitmap?>
        get() = _removbgBitmap.asStateFlow()
    val optBitmap: StateFlow<ImageBitmap?>
        get() = _optBitmap.asStateFlow()
    val navController = _navController.asStateFlow()
    val picSizeType = _picType.asStateFlow()
    val printSizeType = _printType.asStateFlow()
    val bgColor = _bgColor.asStateFlow()

    fun setReginBitmap(bitmap: Bitmap){
        _regionBitmap.update { bitmap.asImageBitmap() }
        _removbgBitmap.update { bitmap.asImageBitmap() }
    }

    fun setRemoveBgBitmap(bitmap: Bitmap){
        _removbgBitmap.update { bitmap.asImageBitmap() }
    }
    fun setOptBitmap(bitmap: Bitmap, bgColor: Color? = null){
        _optBitmap.update { bitmap.asImageBitmap() }
        bgColor?.let { _bgColor.update { bgColor } }
    }

    fun setNavController(navController: NavHostController){
        _navController.update { navController }
    }

    fun setPicSizeType(type: PicSizeType){
        _picType.update { type }
    }

    fun setPrintSizeType(type: PicSizeType){
        _printType.update { type }
    }
}

