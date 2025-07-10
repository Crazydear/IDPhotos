package icu.hearme.idphoto.page

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import icu.hearme.idphoto.R
import icu.hearme.idphoto.adapter.BitmapPrinter.printDocument
import icu.hearme.idphoto.utils.prepareImagesForPrinting
import icu.hearme.idphoto.view.LoadingIndicator
import icu.hearme.idphoto.view.SelectedImageGrid
import icu.hearme.paperprinthelper.view.EmptyState
import icu.hearme.paperprinthelper.view.PrintPreviewScreen
import kotlinx.coroutines.launch
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperPrintApp(initialUris: List<Uri> = emptyList()) {
    val context = LocalContext.current
    var selectedUris by remember { mutableStateOf<List<Uri>>(initialUris) }
    var isProcessing by remember { mutableStateOf(false) }
    var printPreview by remember { mutableStateOf<List<Bitmap>?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 12),
        onResult = { uris -> selectedUris = uris ?: emptyList() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("试卷打印助手", fontWeight = FontWeight.Bold) },
                windowInsets = WindowInsets(10, 0, 0, 0)
            )
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                FloatingActionButton(
                    onClick = {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Default.Add, "选择图片")
                }

                if (selectedUris.isNotEmpty() && !isProcessing) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                isProcessing = true
                                printPreview = prepareImagesForPrinting(context, selectedUris)
                                isProcessing = false

                                // 打印文档
                                printDocument(context, printPreview ?: emptyList())
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Image(painter = painterResource(R.drawable.ic_print), "打印")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
        ) {
            when {
                isProcessing -> {
                    LoadingIndicator()
                }
                printPreview != null -> {
                    PrintPreviewScreen(printPreview!!)
                }
                selectedUris.isNotEmpty() -> {
                    SelectedImageGrid(uris = selectedUris)
                }
                else -> {
                    EmptyState(onSelectImages = {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    })
                }
            }
        }
    }
}
