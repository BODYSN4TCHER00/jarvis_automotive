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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import coil.compose.AsyncImage
import com.example.jarvis.data.models.Tool as ToolModel
import com.example.jarvis.network.WebSocketClient
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToolsViewModel : ViewModel() {
    // Lista observable de tools
    var tools = mutableStateListOf<ToolModel>()
        private set

    init {
        Log.d("ToolsViewModel", "init: ViewModel inicializado")
        // Escuchar actualizaciones en tiempo real
        WebSocketClient.onToolsUpdate = { newTools ->
            Log.d("ToolsViewModel", "Callback onToolsUpdate recibido: ${newTools.size} elementos: $newTools")
            viewModelScope.launch(Dispatchers.Main) {
                tools.clear()
                tools.addAll(newTools)
                Log.d("ToolsViewModel", "Lista interna actualizada: ${tools.size} elementos")
            }
        }
    }
}

@Composable
fun ToolsScreen(navController: NavController) {
    Log.d("ToolsScreen", "Composable ToolsScreen inicializado")
    val viewModel: ToolsViewModel = viewModel()
    val searchQuery = remember { mutableStateOf("") }
    val tools = viewModel.tools
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            Log.d("ToolsScreen", "Renderizando tools: ${tools.size} elementos")
            items(tools.chunked(2)) { rowTools ->
                Log.d("ToolsScreen", "Renderizando fila con ${rowTools.size} elementos: $rowTools")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowTools.forEach { tool ->
                        ToolCard(
                            imageUrl = tool.url,
                            title = tool.name,
                            subtitle = tool.model,
                            battery = tool.battery,
                            temperature = tool.temperature,
                            status = when (tool.availability) {
                                "available" -> ToolStatus.OK
                                "in_use" -> ToolStatus.WARNING
                                else -> ToolStatus.ERROR
                            },
                            modifier = Modifier.weight(1f)
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
