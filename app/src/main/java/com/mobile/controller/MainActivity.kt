package com.mobile.controller

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView

import com.mobile.controller.handlers.OpenCameraHandler
import com.mobile.controller.handlers.TakePhotoHandler
import com.mobile.controller.handlers.GetPropertiesHandler

import com.mobile.controller.network.WebServer
import com.mobile.controller.network.ApiRouter
import com.mobile.controller.network.RequestFactory

import com.mobile.controller.ui.theme.ControllerTheme

class MainActivity : ComponentActivity() {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { entry ->
                Log.d("MainActivity", "${entry.key} granted=${entry.value}")
            }
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                Log.d("MainActivity", "All permissions granted.")
            } else {
                Log.d("MainActivity", "Some permissions were denied.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
        val previewView = PreviewView(this)

        val handlers = listOf(
            GetPropertiesHandler(),
            OpenCameraHandler(this, this, previewView),
            TakePhotoHandler(this, this)
        )

        val requestFactory = RequestFactory()
        val router = ApiRouter(handlers, requestFactory)

        val webServer = WebServer(50012, router)
        webServer.start()

        Log.d("MainActivity", "Web server started")

        enableEdgeToEdge()
        setContent {
            ControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ControllerTheme {
            Greeting("Android")
        }
    }
}
