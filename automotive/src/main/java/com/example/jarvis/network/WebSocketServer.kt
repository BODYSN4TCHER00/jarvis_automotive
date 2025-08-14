package com.example.jarvis.network

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.*
// --- IMPORTS AÑADIDOS ---
import org.java_websocket.drafts.Draft
import org.java_websocket.handshake.ServerHandshakeBuilder
import java.nio.channels.NotYetConnectedException

class WebSocketServer(
    private val port: Int = 3000,
    private val onStatusChanged: (String) -> Unit = { }
) : org.java_websocket.server.WebSocketServer(InetSocketAddress("0.0.0.0", port)) {

    // --- CAMBIO 1: LISTA DE IPs PERMITIDAS ---
    // IPs permitidas para conexión
    private val allowedIps = setOf("192.168.1.64", "127.0.0.1", "10.0.2.2")
    // -----------------------------------------

    private val TAG = "WebSocketServer"
    private val connectedClients = ConcurrentHashMap<String, WebSocket>()
    private val heartbeatJob = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var heartbeatActive = false

    init {
        this.setConnectionLostTimeout(30)
        this.setTcpNoDelay(true)
        this.setReuseAddr(true)
    }

    // --- CAMBIO 2: MÉTODO DE VERIFICACIÓN DE IP ---
    override fun onWebsocketHandshakeReceivedAsServer(
        conn: WebSocket,
        draft: Draft,
        request: ClientHandshake
    ): ServerHandshakeBuilder {
        val remoteIp = conn.remoteSocketAddress.address.hostAddress
        Log.d(TAG, "🔍 Verificando conexión de: $remoteIp")

        if (remoteIp !in allowedIps) {
            Log.w(TAG, "🚫 Conexión rechazada para IP no autorizada: $remoteIp")
            throw NotYetConnectedException()
        }

        Log.d(TAG, "👍 Conexión autorizada para IP: $remoteIp")
        return super.onWebsocketHandshakeReceivedAsServer(conn, draft, request)
    }
    // ----------------------------------------------

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val clientId = "${conn.remoteSocketAddress.address.hostAddress}:${conn.remoteSocketAddress.port}"
        Log.d(TAG, "✅ Cliente conectado: $clientId")
        Log.d(TAG, "✅ User-Agent: ${handshake.getFieldValue("User-Agent")}")
        Log.d(TAG, "✅ Headers: ${handshake.resourceDescriptor}")

        connectedClients[clientId] = conn
        onStatusChanged("Cliente conectado desde $clientId")

        conn.send("Conexión a Automotive exitosa - Cliente: $clientId")

        if (!heartbeatActive) {
            startHeartbeat()
        }

        Log.d(TAG, "📊 Total de clientes conectados: ${connectedClients.size}")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        val clientId = "${conn.remoteSocketAddress.address.hostAddress}:${conn.remoteSocketAddress.port}"
        Log.d(TAG, "❌ Cliente desconectado: $clientId")
        Log.d(TAG, "❌ Razón: $reason (código: $code)")
        Log.d(TAG, "❌ Remoto: $remote")

        connectedClients.remove(clientId)
        onStatusChanged("Cliente desconectado: $reason")

        Log.d(TAG, "📊 Total de clientes conectados: ${connectedClients.size}")

        if (connectedClients.isEmpty()) {
            stopHeartbeat()
        }
    }

    override fun onMessage(conn: WebSocket, message: String) {
        val clientId = "${conn.remoteSocketAddress.address.hostAddress}:${conn.remoteSocketAddress.port}"
        Log.d(TAG, "📨 Mensaje recibido de $clientId: $message")

        when {
            message.startsWith("PING") -> {
                Log.d(TAG, "💓 PING recibido de $clientId, enviando PONG")
                conn.send("PONG")
                return
            }
            message.startsWith("PONG") -> {
                Log.d(TAG, "💓 PONG recibido de $clientId")
                return
            }
            message.startsWith("HEARTBEAT") -> {
                Log.d(TAG, "💓 Heartbeat recibido de $clientId")
                conn.send("HEARTBEAT_ACK")
                return
            }
        }

        onStatusChanged("Mensaje recibido de $clientId: $message")

        conn.send("Echo: $message")

        broadcastToOthers(conn, "Broadcast: $message")
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        val clientInfo = conn?.let { "${it.remoteSocketAddress.address.hostAddress}:${it.remoteSocketAddress.port}" } ?: "Desconocido"
        Log.e(TAG, "💥 Error en WebSocket para cliente $clientInfo: ${ex.message}")
        Log.e(TAG, "Stack trace:", ex)
        onStatusChanged("Error con cliente $clientInfo: ${ex.message}")
    }

    override fun onStart() {
        Log.d(TAG, "🚀 Servidor iniciado en puerto $port")
        Log.d(TAG, "🚀 Escuchando en: ${address.address.hostAddress}:${address.port}")
        onStatusChanged("Servidor iniciado en puerto $port - Esperando conexiones...")
    }

    private fun startHeartbeat() {
        if (heartbeatActive) return

        heartbeatActive = true
        heartbeatJob.launch {
            Log.d(TAG, "💓 Iniciando heartbeat del servidor...")
            while (heartbeatActive && connectedClients.isNotEmpty()) {
                try {
                    delay(30000)

                    if (connectedClients.isNotEmpty()) {
                        Log.d(TAG, "💓 Enviando heartbeat a ${connectedClients.size} clientes...")
                        val heartbeatMessage = "HEARTBEAT_${System.currentTimeMillis()}"

                        connectedClients.values.forEach { client ->
                            try {
                                if (client.isOpen) {
                                    client.send(heartbeatMessage)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "💥 Error enviando heartbeat a cliente: ${e.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "💥 Error en heartbeat del servidor: ${e.message}")
                    break
                }
            }
            Log.d(TAG, "💓 Heartbeat del servidor detenido")
        }
    }

    private fun stopHeartbeat() {
        heartbeatActive = false
        Log.d(TAG, "💓 Deteniendo heartbeat del servidor...")
    }

    private fun broadcastToOthers(sender: WebSocket, message: String) {
        val senderId = "${sender.remoteSocketAddress.address.hostAddress}:${sender.remoteSocketAddress.port}"
        var sentCount = 0

        connectedClients.forEach { (clientId, client) ->
            if (clientId != senderId && client.isOpen) {
                try {
                    client.send(message)
                    sentCount++
                } catch (e: Exception) {
                    Log.e(TAG, "💥 Error enviando broadcast a $clientId: ${e.message}")
                }
            }
        }

        if (sentCount > 0) {
            Log.d(TAG, "📡 Broadcast enviado a $sentCount clientes")
        }
    }

    fun sendMessage(message: String) {
        if (connectedClients.isEmpty()) {
            onStatusChanged("Error: No hay clientes conectados")
            return
        }

        var sentCount = 0
        val failedClients = mutableListOf<String>()

        connectedClients.forEach { (clientId, client) ->
            if (client.isOpen) {
                try {
                    client.send(message)
                    sentCount++
                    Log.d(TAG, "📤 Mensaje enviado a $clientId: $message")
                } catch (e: Exception) {
                    Log.e(TAG, "💥 Error enviando mensaje a $clientId: ${e.message}")
                    failedClients.add(clientId)
                }
            } else {
                Log.w(TAG, "⚠️ Cliente $clientId no está abierto")
                failedClients.add(clientId)
            }
        }

        failedClients.forEach { clientId ->
            connectedClients.remove(clientId)
            Log.d(TAG, "🧹 Cliente $clientId removido por fallo")
        }

        if (sentCount > 0) {
            onStatusChanged("Mensaje enviado a $sentCount clientes: $message")
        } else {
            onStatusChanged("Error: No se pudo enviar mensaje a ningún cliente")
        }

        Log.d(TAG, "📊 Resumen envío: $sentCount exitosos, ${failedClients.size} fallidos")
    }

    fun isClientConnected(): Boolean = connectedClients.isNotEmpty()

    fun startServer() {
        try {
            if (!isPortAvailable(port)) {
                throw Exception("Puerto $port ya está en uso")
            }

            super.start()
            Log.d(TAG, "✅ Servidor WebSocket iniciado exitosamente en puerto $port")
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error iniciando servidor: ${e.message}")
            onStatusChanged("Error iniciando servidor: ${e.message}")
            throw e
        }
    }

    fun stopServer() {
        try {
            Log.d(TAG, "🛑 Deteniendo servidor WebSocket...")

            stopHeartbeat()

            connectedClients.values.forEach { client ->
                try {
                    client.close()
                } catch (e: Exception) {
                    Log.e(TAG, "💥 Error cerrando cliente: ${e.message}")
                }
            }
            connectedClients.clear()

            super.stop()
            onStatusChanged("Servidor detenido")
            Log.d(TAG, "✅ Servidor WebSocket detenido")
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error deteniendo servidor: ${e.message}")
            onStatusChanged("Error deteniendo servidor: ${e.message}")
        }
    }

    private fun isPortAvailable(port: Int): Boolean {
        return try {
            ServerSocket(port).use { true }
        } catch (e: Exception) {
            false
        }
    }

    fun getConnectedClientsCount(): Int = connectedClients.size

    fun getConnectedClientsInfo(): List<String> {
        return connectedClients.keys.toList()
    }

    fun cleanup() {
        heartbeatJob.cancel()
        stopHeartbeat()
    }
}