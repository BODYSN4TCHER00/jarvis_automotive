package com.example.jarvis.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jarvis.R
import com.example.jarvis.components.ui.*

@Composable
fun JobsScreen(navController: NavController) {
    val isRootScreen = navController.previousBackStackEntry == null

    // Bloquea el botón físico de regreso solo si es pantalla raíz
    BackHandler(enabled = isRootScreen) {
        // No hacer nada si es la primera pantalla
    }

    val searchQuery = remember { mutableStateOf("") }

    val jobs = listOf(
        JobItem("Mantenimiento Preventivo En Planta De Producción", Icons.Filled.Build),
        JobItem("Instalación De Tablero Eléctrico En Nave Industrial", Icons.Filled.Bolt),
        JobItem("Sustitución De Luminarias LED En Almacén", Icons.Filled.Bolt),
        JobItem("Reemplazo De Bombas Hidráulicas", Icons.Filled.Handyman),
        JobItem("Alineación De Ejes Y Motores Industriales", Icons.Filled.Handyman)
    )

    ScreenContainer {
        TopBar(
            showBack = !isRootScreen, // ✅ Oculta flecha si es pantalla raíz
            onBack = { navController.popBackStack() },
            searchQuery = searchQuery.value,
            onSearchChange = { searchQuery.value = it },
            logoResId = R.drawable.ic_logo,
            onJobsClick = { navController.navigate("jobs") },
            onToolsClick = { navController.navigate("tools") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle(text = "Trabajos")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(jobs) { job ->
                ListItem(
                    icon = job.icon,
                    text = job.title,
                    onClick = {
                        navController.navigate("jobTools/${job.title}")
                    }
                )
            }
        }
    }
}

data class JobItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)