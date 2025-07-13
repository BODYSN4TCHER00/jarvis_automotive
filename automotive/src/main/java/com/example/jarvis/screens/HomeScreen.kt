package com.example.jarvis.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jarvis.components.ui.TopBar
import com.example.jarvis.components.ui.ScreenContainer
import com.example.jarvis.R

@Composable
fun HomeScreen(navController: NavController) {
    ScreenContainer {
        TopBar(
            logoResId = R.drawable.ic_logo,
            onJobsClick = { navController.navigate("jobs") },
            onToolsClick = { navController.navigate("tools") }
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Bienvenido a Jarvis Auto",
                fontSize = 26.sp,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { navController.navigate("tools") },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(80.dp)
            ) {
                Text("Ir a Herramientas", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {navController.navigate("jobs")},
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(80.dp)
            ) {
                Text("Ir a Trabajos", fontSize = 22.sp)
            }
        }
    }
}
