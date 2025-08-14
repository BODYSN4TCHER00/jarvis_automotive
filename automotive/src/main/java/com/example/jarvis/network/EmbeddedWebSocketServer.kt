package com.example.jarvis.network

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class EmbeddedWebSocketServer(port: Int = 8081)
    : WebSocketServer(InetSocketAddress("127.0.0.1", port)) {

    override fun onStart() {
        isReuseAddr = true
        Log.d(TAG, "WS up ws://127.0.0.1:${address.port}")
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        Log.d(TAG, "client connected ${conn.remoteSocketAddress}")
        ConnectionBus.onClientConnected()
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        Log.d(TAG, "client closed ${conn.remoteSocketAddress} code=$code reason=$reason")
        ConnectionBus.onClientClosed()
    }

    override fun onMessage(conn: WebSocket, message: String) {
        Log.d(TAG, "msg: $message")
        ConnectionBus.onMessage(message)
        val m = message.trim().lowercase()
        if (m.contains("\"type\":\"ping\"")) {
            conn.send("""{"type":"pong","from":"car","ok":true}""")
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        Log.e(TAG, "error: ${ex.message}", ex)
    }

    companion object { const val TAG = "WS_SERVER" }
}