package icu.hearme.idphoto.fram

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import icu.hearme.idphoto.enums.PicSizeType
import icu.hearme.idphoto.model.IDViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarSelectionActions(selectedItems: Int, viewModel: IDViewModel = viewModel()) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val title1 by viewModel.printSizeType.collectAsState()
    val menuItems = listOf(PicSizeType.ThreeInch, PicSizeType.FourInch)

    when (selectedItems) {
        0 -> {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("首页", fontSize = 36.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                windowInsets = WindowInsets(0,0,0,0)
            )
        }
        1 -> {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("调整背景色", fontSize = 36.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                windowInsets = WindowInsets(0,0,0,0)
            )
        }
        2 -> {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("裁剪", fontSize = 36.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                windowInsets = WindowInsets(0,0,0,0)
            )
        }
        3 -> {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("预览图", fontSize = 36.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                actions = {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Row(modifier = Modifier.clickable { expanded = !expanded }) {
                            Text(
                                title1.value,
                                fontSize = 22.sp,
                                modifier = Modifier,
                                textAlign = TextAlign.Center
                            )
                            Icon(
                                if (!expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                ""
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            offset = DpOffset(50.dp, 0.dp)
                        ) {
                            menuItems.forEachIndexed { index, section ->
                                DropdownMenuItem(
                                    text = { Text(section.value) },
                                    onClick = {
                                        viewModel.setPrintSizeType(section)
                                        expanded = false
                                    },
                                    modifier = Modifier.heightIn(20.dp, 35.dp)
                                )
                            }
                        }
                    }
                },
                windowInsets = WindowInsets(0,0,0,0)
            )
        }
    }
}