package com.example.jarvis.network

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import org.json.JSONObject

/**
 * Cliente WebSocket del CARRO (Automotive).
 * Se conecta al MISMO endpoint que la app móvil:
 *   ws://192.168.1.74:8080/chat
 * Escucha mensajes (JSON) y los aplica al SyncStore para que la UI se actualice.
 */
object WebSocketClient {

    private const val SERVER_URL = "ws://192.168.1.74:8080/chat"

    private val client = HttpClient(OkHttp) {
        install(WebSockets)
        install(Logging) { level = LogLevel.ALL }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null
    private var session: DefaultClientWebSocketSession? = null

    fun connect(clientType: String = "car") {
        if (job?.isActive == true && session != null) {
            Log.d("CAR_WS", "Ya conectado")
            return
        }
        job = scope.launch {
            try {
                client.webSocket(SERVER_URL) {
                    session = this
                    Log.d("CAR_WS", "Conectado a $SERVER_URL")

                    send(Frame.Text("IDENTIFY:$clientType"))

                    // Loop de recepción
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            Log.d("CAR_WS_RX", text)


                            try {
                                val json = JSONObject(text)
                                SyncStore.applyIncomingJson(json)
                            } catch (_: Exception) {

                            }
                        }
                    }
                }
            } catch (ce: CancellationException) {
                Log.d("CAR_WS", "Conexión cancelada")
            } catch (e: Exception) {
                Log.e("CAR_WS", "Error WS: ${e.message}", e)
            } finally {
                session = null
                Log.d("CAR_WS", "Sesión WS cerrada")
            }
        }
    }

    fun disconnect() {
        scope.launch {
            try { session?.close(CloseReason(CloseReason.Codes.NORMAL, "bye")) } catch (_: Exception) {}
            session = null
            job?.cancel()
            job = null
            Log.d("CAR_WS", "Desconectado")
        }
    }
}