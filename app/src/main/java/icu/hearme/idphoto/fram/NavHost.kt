package icu.hearme.idphoto.fram

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import icu.hearme.idphoto.model.IDViewModel
import icu.hearme.idphoto.page.MainPage
import icu.hearme.idphoto.page.PaperPrintApp
import icu.hearme.idphoto.page.PhotoBackgroundChanger
import icu.hearme.idphoto.page.PhotoCropperPage
import icu.hearme.idphoto.page.PhotoPreviewPage
import icu.hearme.idphoto.view.LockScreenOrientation

@Composable
fun NavHosts(viewModel: IDViewModel, modifier: Modifier = Modifier){
    val navController by viewModel.navController.collectAsState()
    val windowSize = with(LocalDensity.current) { currentWindowSize().toSize().toDpSize() }
    val layoutType = if (windowSize.width >= 1200.dp || windowSize.width > windowSize.height){
    } else {
        LockScreenOrientation(true)
    }
    NavHost(navController ?: rememberNavController(), startDestination = "homeMain", modifier = modifier){
        navigation(startDestination = "home", route = "homeMain") {
            composable("home") {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppBarSelectionActions(0)
                    MainPage(viewModel,Modifier)
                }
            }

            composable("changebg") {
                Column {
                    AppBarSelectionActions(1)
                    PhotoBackgroundChanger(viewModel)
                }
            }

            composable("crop") {
                BackHandler {
                    navController?.navigate("home") {
                        popUpTo("homeMain") { inclusive = true }
                    }
                }
                Column {
                    AppBarSelectionActions(2)
                    PhotoCropperPage(viewModel)
                }
            }

            composable("preview") {
                Column {
                    AppBarSelectionActions(3, viewModel)
                    PhotoPreviewPage(viewModel)
                }
            }
        }
        navigation(startDestination = "paperprint", route = "PaperPrintHelper"){
            composable("paperprint") {
                PaperPrintApp()
            }
        }
    }
}