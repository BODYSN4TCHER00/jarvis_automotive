package com.example.jarvis.network
//mostrar logs en consola
import android.util.Log
//cliente HTTP de Ktor con motor OkHttp
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
//registrar las peticiones y respuestas HTTP
import io.ktor.client.plugins.logging.*
//manejo de websockets
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.example.jarvis.data.models.Job as JobModel
import com.example.jarvis.data.models.Tool
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object WebSocketClient {

    // Dirección del servidor WebSocket
    //10.0.2.2 es la dirección IP del emulador
    //8080 es el puerto del servidor
    private const val SERVER_URL = "ws://10.0.2.2:8080/chat"

    // Cliente Ktor
    //motor de red
    private val client = HttpClient(OkHttp) {
        //soporte websocket en el cliente
        install(WebSockets)
        //registra todas las solicitudes y mensajes
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    //hilo de conexión
    private var job: Job? = null
    //guardar el tipo de cliente
    private var clientType: String = "Desconocido"

    private val gson = Gson()

    private var lastTools: List<Tool> = emptyList()
    private var lastJobs: List<JobModel> = emptyList()

    // Callbacks para notificar a la UI/ViewModel
    var onToolsUpdate: ((List<Tool>) -> Unit)? = null
        set(value) {
            field = value
            Log.d("WebSocket", "Callback onToolsUpdate asignado: ${value != null}")
            if (value != null && lastTools.isNotEmpty()) {
                value(lastTools)
            }
        }
    var onJobsUpdate: ((List<JobModel>) -> Unit)? = null
        set(value) {
            field = value
            Log.d("WebSocket", "Callback onJobsUpdate asignado: ${value != null}")
            if (value != null && lastJobs.isNotEmpty()) {
                value(lastJobs)
            }
        }

    // Función para iniciar conexión
    fun connect(type: String) {
        Log.d("WebSocket", "Iniciando conexión con tipo: $type")
        clientType = type

        //evita conexiones duplicadas
        if (job?.isActive == true) {
            Log.d("WebSocket", "Ya está conectado")
            return
        }
        //lanza una corutina en hilo de entrada/salida (Dispatchers.IO).
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("WebSocket", "Intentando abrir websocket a $SERVER_URL")
                //abre la conexion websocket a SERVER_URL
                client.webSocket(SERVER_URL) {
                    Log.d("WebSocket", "Conectado al servidor")

                    // Mandar identificación primero
                    send("IDENTIFY:$clientType")
                    send("Hola Mobile")

                    // Escuchar mensajes entrantes
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            Log.d("WebSocket", "Mensaje recibido: $message")
                            //muestra el mensaje recibido
                            Log.d("WebSocket", "$message")
                            // Procesar mensajes de tools y jobs
                            try {
                                val json = gson.fromJson(message, Map::class.java)
                                Log.d("WebSocket", "JSON parseado: $json")
                                when (json["type"]) {
                                    "update_tools" -> {
                                        val dataJson = gson.toJson(json["data"])
                                        val listType = object : TypeToken<List<Tool>>() {}.type
                                        val tools: List<Tool> = gson.fromJson(dataJson, listType)
                                        Log.d("WebSocket-RECV", "Recibido update_tools: $tools")
                                        onToolsUpdate?.invoke(tools)
                                    }
                                    "update_jobs" -> {
                                        val dataJson = gson.toJson(json["data"])
                                        val listType = object : TypeToken<List<JobModel>>() {}.type
                                        val jobs: List<JobModel> = gson.fromJson(dataJson, listType)
                                        Log.d("WebSocket-RECV", "Recibido update_jobs: $jobs")
                                        onJobsUpdate?.invoke(jobs)
                                    }
                                    // Manejo de mensajes desde móvil
                                    "tools" -> {
                                        val dataJson = gson.toJson(json["data"])
                                        val listType = object : TypeToken<List<Tool>>() {}.type
                                        val tools: List<Tool> = gson.fromJson(dataJson, listType)
                                        Log.d("WebSocket-RECV", "Recibido tools (desde móvil): $tools")
                                        lastTools = tools
                                        onToolsUpdate?.invoke(tools)
                                        Log.d("WebSocket-UI", "Automotive recibió tools: $tools")
                                    }
                                    "jobs" -> {
                                        val dataJson = gson.toJson(json["data"])
                                        val listType = object : TypeToken<List<JobModel>>() {}.type
                                        val jobs: List<JobModel> = gson.fromJson(dataJson, listType)
                                        Log.d("WebSocket-RECV", "Recibido jobs (desde móvil): $jobs")
                                        lastJobs = jobs
                                        onJobsUpdate?.invoke(jobs)
                                        Log.d("WebSocket-UI", "Automotive recibió jobs: $jobs")
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("WebSocket", "Error procesando mensaje: ${e.message}. Mensaje: $message")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "Error de conexión: ${e.message}")
            }
        }
    }

    // Función para cerrar conexión
    fun disconnect() {
        job?.cancel()
        Log.d("WebSocket", "Conexión cerrada para $clientType")
    }
}
