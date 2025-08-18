package com.example.jarvis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.jarvis.components.navigation.NavigationComponent
import com.example.jarvis.components.theme.JarvisAppTheme
import com.example.jarvis.network.WebSocketClient

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JarvisAppTheme {
                NavigationComponent()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        WebSocketClient.connect(clientType = "Automotive")
    }

    override fun onStop() {
        WebSocketClient.disconnect()
        super.onStop()
    }
}