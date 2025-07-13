package com.example.jarvis.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning

@Composable
fun ToolCard(
    image: Painter,
    title: String,
    subtitle: String,
    battery: Int,
    temperature: Int,
    status: ToolStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BatteryStatus(battery)
                        Spacer(modifier = Modifier.width(8.dp))
                        TemperatureStatus(temperature)
                    }
                }
            }
            // Status Icon
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                when (status) {
                    ToolStatus.OK -> Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                    ToolStatus.WARNING -> Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFFC107))
                    ToolStatus.ERROR -> Icon(Icons.Filled.Error, contentDescription = null, tint = Color(0xFFF44336))
                }
            }
        }
    }
}

enum class ToolStatus { OK, WARNING, ERROR }

@Composable
fun BatteryStatus(percent: Int) {
    val color = when {
        percent >= 80 -> Color(0xFF4CAF50)
        percent >= 30 -> Color(0xFFFFA000)
        else -> Color(0xFFF44336)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp, 8.dp)
                .background(color, RoundedCornerShape(2.dp))
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
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "${temp}°C", fontSize = 13.sp, color = Color.Black)
    }
} 