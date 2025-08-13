package com.example.jarvis.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ConnectionBus {
    // Número de clientes conectados (0 = sin conexión)
    private val _connectedClients = MutableStateFlow(0)
    val connectedClients: StateFlow<Int> = _connectedClients

    // Último mensaje recibido (opcional, útil en pruebas)
    private val _lastMessage = MutableStateFlow<String?>(null)
    val lastMessage: StateFlow<String?> = _lastMessage

    internal fun onClientConnected() {
        _connectedClients.value = (_connectedClients.value + 1).coerceAtLeast(0)
    }

    internal fun onClientClosed() {
        _connectedClients.value = (_connectedClients.value - 1).coerceAtLeast(0)
    }

    internal fun onMessage(msg: String) {
        _lastMessage.value = msg
    }
}

