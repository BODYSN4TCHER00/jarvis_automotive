package com.example.jarvis.components.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToolCard(
    image: Painter,
    title: String,
    subtitle: String,
    battery: Int,
    temperature: Int,
    status: ToolStatus,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .width(180.dp) // más cuadrado
            .height(150.dp) // más alto
            .padding(6.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BatteryStatus(battery)
                    TemperatureStatus(temperature)
                }
            }

            StatusIcon(
                status = status,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            )
        }
    }
}

enum class ToolStatus { OK, WARNING, ERROR }

@Composable
fun StatusIcon(status: ToolStatus, modifier: Modifier = Modifier) {
    val (icon, color) = when (status) {
        ToolStatus.OK -> Icons.Filled.CheckCircle to Color(0xFF4CAF50)
        ToolStatus.WARNING -> Icons.Filled.Warning to Color(0xFFFFC107)
        ToolStatus.ERROR -> Icons.Filled.Error to Color(0xFFF44336)
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = modifier.size(20.dp)
    )
}

@Composable
fun BatteryStatus(percent: Int) {
    val color = when {
        percent >= 80 -> Color(0xFF4CAF50)
        percent >= 30 -> Color(0xFFFFA000)
        else -> Color(0xFFF44336)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.BatteryStd,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$percent%", fontSize = 13.sp, color = Color.Black)
    }
}

@Composable
fun TemperatureStatus(temp: Int) {
    val color = when {
        temp < 30 -> Color(0xFF1976D2)
        temp < 60 -> Color(0xFFFFA000)
        else -> Color(0xFFF44336)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Thermostat,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "${temp}°C", fontSize = 13.sp, color = Color.Black)
    }
}
