package com.example.jarvis.components.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cases
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    searchQuery: String? = null,
    onSearchChange: ((String) -> Unit)? = null,
    placeholder: String = "Buscar",
    @DrawableRes logoResId: Int? = null,
    onJobsClick: (() -> Unit)? = null,
    onToolsClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        if (showBack && onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "back")
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        // Search bar o espacio
        if (searchQuery != null && onSearchChange != null) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text(placeholder) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                singleLine = true
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        // Iconos de navegación y logo alineados a la derecha
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onToolsClick?.invoke() }) {
                Icon(
                    Icons.Filled.Build,
                    contentDescription = "tools",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { onJobsClick?.invoke() }) {
                Icon(
                    Icons.Filled.Cases,
                    contentDescription = "jobs",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (logoResId != null) {
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
    Divider()
} 