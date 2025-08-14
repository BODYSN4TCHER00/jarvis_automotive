package com.example.jarvis.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jarvis.network.NetworkViewModel

@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel = viewModel()
) {
    val isServerRunning by viewModel.isServerRunning.collectAsState()
    val isClientConnected by viewModel.isClientConnected.collectAsState()
    val serverIp by viewModel.serverIp.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Servidor WebSocket",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isClientConnected -> Color(0xFFE8F5E8) // Verde cuando conectado
                    isServerRunning -> Color(0xFFFFF3E0) // Naranja cuando servidor activo pero sin cliente
                    else -> Color(0xFFFFEBEE) // Rojo cuando inactivo
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de estado
                Icon(
                    imageVector = when {
                        isClientConnected -> Icons.Default.CheckCircle
                        isServerRunning -> Icons.Default.Schedule
                        else -> Icons.Default.Error
                    },
                    contentDescription = "Status",
                    tint = when {
                        isClientConnected -> Color.Green
                        isServerRunning -> Color(0xFFFF9800) // Naranja
                        else -> Color.Red
                    },
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Estado del servidor
                Text(
                    text = when {
                        isClientConnected -> "Cliente Conectado"
                        isServerRunning -> "Servidor Activo"
                        else -> "Servidor Inactivo"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // IP del servidor
                if (serverIp != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "URL del Servidor:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Blue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ws://$serverIp:3000",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Blue,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Copia esta URL en la app móvil",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else if (isServerRunning) {
                    Text(
                        text = "IP no disponible",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Mensaje de estado
                Text(
                    text = statusMessage,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.startServer() },
                enabled = !isServerRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { viewModel.stopServer() },
                enabled = isServerRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Stop")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Detener")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Send Message Button
        Button(
            onClick = { viewModel.sendTestMessage() },
            enabled = isClientConnected,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviar Mensaje de Prueba", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info text
        if (isServerRunning && !isClientConnected) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Blue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esperando conexión del dispositivo móvil...",
                        fontSize = 14.sp,
                        color = Color.Blue,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Asegúrate de que ambos dispositivos estén en la misma red WiFi",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (!isServerRunning) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFFFF9800), // Naranja
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Presiona 'Iniciar' para activar el servidor",
                    fontSize = 14.sp,
                        color = Color(0xFFFF9800), // Naranja
                        textAlign = TextAlign.Center
                )
                }
            }
        }
    }
} 