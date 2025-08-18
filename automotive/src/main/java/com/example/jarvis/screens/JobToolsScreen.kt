package com.example.jarvis.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cases
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jarvis.components.ui.TopBar
import com.example.jarvis.components.ui.ToolCard
import com.example.jarvis.components.ui.ToolStatus
import com.example.jarvis.components.ui.ScreenContainer
import com.example.jarvis.R

// Datos de ejemplo para herramientas asignadas
private data class ToolData(
    val title: String,
    val subtitle: String,
    val battery: Int,
    val temperature: Int,
    val status: ToolStatus
)

private val assignedToolsSample = listOf(
    ToolData("Destornillador Electrico", "ATI-20", 20, 15, ToolStatus.OK),
    ToolData("Sierra Sable", "M18", 20, 75, ToolStatus.ERROR),
    ToolData("Engrapadora De Corona", "XTS01Z", 100, 29, ToolStatus.OK),
    ToolData("Taladro Inalambrico", "DeWalt DCD791D2", 20, 29, ToolStatus.ERROR)
)

@Composable
fun JobToolsScreen(navController: NavController, jobName: String) {
    ScreenContainer {
        TopBar(
            showBack = true,
            onBack = { navController.popBackStack() },
            logoResId = R.drawable.ic_logo,
            onJobsClick = { navController.navigate("jobs") },
            onToolsClick = { navController.navigate("tools") }
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Título y acciones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Handyman, contentDescription = null, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = jobName,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /* Eliminar trabajo */ }) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar trabajo")
            }
            IconButton(onClick = { /* Editar trabajo */ }) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar trabajo")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Lista de herramientas
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
        ) {
            assignedToolsSample.chunked(2).forEach { rowTools ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowTools.forEach { tool ->
                        ToolCard(
                            imageUrl = "", // Sin imagen remota, usa la imagen por defecto
                            title = tool.title,
                            subtitle = tool.subtitle,
                            battery = tool.battery,
                            temperature = tool.temperature,
                            status = tool.status,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp)
                        )
                    }
                    if (rowTools.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}