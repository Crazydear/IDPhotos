package icu.hearme.idphoto

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import icu.hearme.idphoto.fram.NavHosts
import icu.hearme.idphoto.model.IDViewModel
import icu.hearme.idphoto.ui.theme.证件照Theme
import icu.hearme.idphoto.view.LockScreenOrientation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySootheApp()
        }
    }
}

@Composable
fun MySootheApp(){
    val navController = rememberNavController()
    val viewModel: IDViewModel = viewModel()

    // 可选：一次性设置 navController
    LaunchedEffect(Unit) {
        viewModel.setNavController(navController)
    }

    证件照Theme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHosts(viewModel,modifier = Modifier.padding(innerPadding))
        }
    }
}
