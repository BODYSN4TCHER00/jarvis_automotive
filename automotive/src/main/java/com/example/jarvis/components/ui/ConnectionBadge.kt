package com.example.jarvis.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jarvis.network.ConnectionBus

@Composable
fun ConnectionBadge(modifier: Modifier = Modifier, padding: PaddingValues = PaddingValues(0.dp)) {
    val clients by ConnectionBus.connectedClients.collectAsState(initial = 0)
    val connected = clients > 0

    val bg = if (connected) Color(0xFF1B5E20) else Color(0xFFB71C1C) // verde/rojo discreto
    val text = if (connected) "WS Conectado ($clients)" else "WS Esperando"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(padding)
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = if (connected) Icons.Default.Wifi else Icons.Default.WifiOff,
            contentDescription = null,
            tint = Color.White
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}