package icu.hearme.idphoto

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import icu.hearme.idphoto.fram.NavHosts
import icu.hearme.idphoto.model.IDViewModel
import icu.hearme.idphoto.page.PaperPrintApp
import icu.hearme.idphoto.ui.theme.证件照Theme

class MainActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            when {
                intent?.action == Intent.ACTION_SEND -> {
                    if (intent.type?.startsWith("image/") == true) {
                        val uri = handleSendImage(intent)
                        uri?.let {
                            PaperPrintApp(listOf(uri))
                        }
                    }
                }
                intent?.action == Intent.ACTION_SEND_MULTIPLE && intent.type?.startsWith("image/") == true -> {
                    val uris = handleSendMultipleImages(intent)
                    uris?.let {
                        PaperPrintApp(it)
                    }
                }
                else -> {
                    MySootheApp()
                }
            }
        }
    }

    private fun handleSendImage(intent: Intent): Uri? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            else -> @Suppress("DEPRECATION")
            intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri
        }
    }

    private fun handleSendMultipleImages(intent: Intent): List<Uri>? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
            else -> @Suppress("DEPRECATION") {
                intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
                    ?.filterIsInstance<Uri>()
                    ?: emptyList()
            }
        }

    }
}

@Composable
fun MySootheApp(){
    val navController = rememberNavController()
    val viewModel: IDViewModel = viewModel()

    证件照Theme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHosts(viewModel,modifier = Modifier.padding(innerPadding), navController)
        }
    }
}
