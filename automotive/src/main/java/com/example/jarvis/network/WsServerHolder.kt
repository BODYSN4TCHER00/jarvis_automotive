package com.example.jarvis.network

object WsServerHolder {
    @Volatile private var server: EmbeddedWebSocketServer? = null

    @Synchronized
    fun ensureStarted(port: Int = 8081) {
        if (server != null) return
        server = EmbeddedWebSocketServer(port).also { it.start() }
    }

    @Synchronized
    fun stop() {
        try { server?.stop(1000) } catch (_: Exception) {}
        server = null
    }
}