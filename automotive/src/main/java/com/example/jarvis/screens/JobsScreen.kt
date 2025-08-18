package com.example.jarvis.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jarvis.R
import com.example.jarvis.components.ui.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableStateListOf
import com.example.jarvis.data.models.Job as JobModel
import com.example.jarvis.network.WebSocketClient
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight

class JobsViewModel : ViewModel() {
    var jobs = mutableStateListOf<JobModel>()
        private set

    init {
        WebSocketClient.onJobsUpdate = { newJobs ->
            viewModelScope.launch(Dispatchers.Main) {
                jobs.clear()
                jobs.addAll(newJobs)
            }
        }
    }
}

@Composable
fun JobsScreen(navController: NavController) {
    val viewModel: JobsViewModel = viewModel()
    val isRootScreen = navController.previousBackStackEntry == null
    BackHandler(enabled = isRootScreen) {}
    val searchQuery = remember { mutableStateOf("") }
    val jobs = viewModel.jobs
    ScreenContainer {
        TopBar(
            showBack = !isRootScreen,
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
                    icon = Icons.Filled.Work,
                    text = {
                        Row {
                            Text(
                                text = job.clientName + ": ",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = job.worksite)
                        }
                    },
                    onClick = {
                        navController.navigate("jobTools/${job.id}")
                    }
                )
            }
        }
    }
}

data class JobItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)