package com.example.jarvis.data.models

data class Tool(
    val id: String = "",
    val availability: String =  "available" ,
    val battery: Int = 0,
    val isActive: Boolean = true,
    val model: String = "",
    val name: String = "",
    val temperature: Int = 0,
    val url: String = ""
) 