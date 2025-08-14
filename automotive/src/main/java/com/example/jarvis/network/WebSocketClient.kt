package com.example.jarvis.network

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

/**
 * Cliente WebSocket para conectar con el servidor Automotive
 * 
 * USO EN APLICACIÓN MÓVIL:
 * 
 * // 1. Agregar dependencias en build.gradle:
 * implementation 'org.java-websocket:Java-WebSocket:1.5.3'
 * 
 * // 2. Agregar permisos en AndroidManifest.xml:
 * <uses-permission android:name="android.permission.INTERNET" />
 * 
 * // 3. Usar en tu Activity:
 * 
 * private lateinit var webSocketClient: WebSocketClient
 * 
 * private fun connectToAutomotive() {
 *     // Reemplaza con la IP mostrada en la aplicación Automotive
 *     val serverUrl = "ws://192.168.1.100:3000"
 *     
 *     webSocketClient = WebSocketClient(
 *         serverUrl = serverUrl,
 *         onMessageReceived = { message ->
 *             // Mostrar mensaje recibido
 *             Log.d("MainActivity", "Mensaje: $message")
 *             runOnUiThread {
 *                 showMessage(message)
 *             }
 *         },
 *         onConnectionEstablished = {
 *             Log.d("MainActivity", "Conectado a Automotive")
 *             runOnUiThread {
 *                 showConnectionStatus("Conectado")
 *             }
 *         },
 *         onConnectionClosed = {
 *             Log.d("MainActivity", "Desconectado de Automotive")
 *             runOnUiThread {
 *                 showConnectionStatus("Desconectado")
 *             }
 *         }
 *     )
 *     
 *     webSocketClient.connect()
 * }
 * 
 * private fun sendMessage(message: String) {
 *     webSocketClient.sendMessage(message)
 * }
 * 
 * override fun onDestroy() {
 *     super.onDestroy()
 *     webSocketClient.close()
 * }
 */
class WebSocketClient(
    private val serverUrl: String,
    private val onMessageReceived: (String) -> Unit = { },
    private val onConnectionEstablished: () -> Unit = { },
    private val onConnectionClosed: () -> Unit = { }
) : org.java_websocket.client.WebSocketClient(URI(serverUrl)) {

    private val TAG = "WebSocketClient"

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d(TAG, "Conectado al servidor Automotive")
        onConnectionEstablished()
    }

    override fun onMessage(message: String?) {
        message?.let { msg ->
            Log.d(TAG, "Mensaje recibido: $msg")
            onMessageReceived(msg)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "Conexión cerrada: $reason")
        onConnectionClosed()
    }

    override fun onError(ex: Exception?) {
        Log.e(TAG, "Error en WebSocket: ${ex?.message}")
        ex?.printStackTrace()
    }

    fun sendMessage(message: String) {
        if (isOpen) {
            send(message)
            Log.d(TAG, "Mensaje enviado: $message")
        } else {
            Log.w(TAG, "No se puede enviar mensaje: conexión cerrada")
        }
    }

    fun isConnected(): Boolean = isOpen
} 