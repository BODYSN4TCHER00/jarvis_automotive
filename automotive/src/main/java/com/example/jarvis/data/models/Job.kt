package com.example.jarvis.data.models

data class Job(
    val id: String = "",
    val clientName: String = "",
    val worksite: String = "",
    val selectedTools: List<String> = emptyList(),
    val status: String = "pending",
    val isActive: Boolean = true
) 