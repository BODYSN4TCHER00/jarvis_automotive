package com.example.jarvis.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cases
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jarvis.components.ui.TopBar
import com.example.jarvis.components.ui.SectionTitle
import com.example.jarvis.components.ui.ScreenContainer
import com.example.jarvis.components.ui.ToolCard
import com.example.jarvis.components.ui.ToolStatus
import com.example.jarvis.R
import androidx.navigation.NavController

// Datos de ejemplo para herramientas
private data class Tool(
    val imageRes: Int,
    val title: String,
    val subtitle: String,
    val battery: Int,
    val temperature: Int,
    val status: ToolStatus
)

private val toolsSample = listOf(
    Tool(R.drawable.ic_logo, "Taladro Inalámbrico", "Bosch GSR 12V", 85, 32, ToolStatus.OK),
    Tool(R.drawable.ic_logo, "Multímetro Digital", "Fluke 117", 60, 28, ToolStatus.WARNING),
    Tool(R.drawable.ic_logo, "Cámara Térmica", "FLIR E4", 25, 65, ToolStatus.ERROR),
    Tool(R.drawable.ic_logo, "Destornillador Eléctrico", "Makita DF012DSE", 90, 29, ToolStatus.OK)
)

@Composable
fun ToolsScreen(navController: NavController) {
    val searchQuery = remember { mutableStateOf("") }
    ScreenContainer {
        TopBar(
            showBack = true,
            onBack = { navController.popBackStack() },
            searchQuery = searchQuery.value,
            onSearchChange = { searchQuery.value = it },
            logoResId = R.drawable.ic_logo,
            onJobsClick = { navController.navigate("jobs") },
            onToolsClick = { navController.navigate("tools") }
        )
        SectionTitle(text = "Herramientas")
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
        ) {
            // Mostrar las herramientas en filas de 2
            toolsSample.chunked(2).forEach { rowTools ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowTools.forEach { tool ->
                        ToolCard(
                            image = painterResource(id = tool.imageRes),
                            title = tool.title,
                            subtitle = tool.subtitle,
                            battery = tool.battery,
                            temperature = tool.temperature,
                            status = tool.status,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Si la fila tiene solo 1 elemento, agregar un Spacer para alinear
                    if (rowTools.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
