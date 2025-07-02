package com.mobile.controller

import com.mobile.controller.handlers.GetPropertiesHandler
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
import com.mobile.controller.api.ApiRouter
import com.mobile.controller.network.WebServer

import com.mobile.controller.ui.theme.ControllerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val handlers = listOf(
            GetPropertiesHandler()
        )
        val router = ApiRouter(handlers)

        val webServer = WebServer(50012, router)
        webServer.start()

        Log.d("MainActivity", "Web server started")

        enableEdgeToEdge()
        setContent {
            ControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
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