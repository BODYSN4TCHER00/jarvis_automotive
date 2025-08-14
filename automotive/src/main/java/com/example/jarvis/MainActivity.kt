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
        // Conectar al servidor al iniciar la app
        WebSocketClient.connect("Automotive")
        setContent {
            JarvisAppTheme {
                NavigationComponent()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Cerrar conexión al salir de la app
        WebSocketClient.disconnect()
    }
}
