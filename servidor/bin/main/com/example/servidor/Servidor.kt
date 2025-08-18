//package com.example.servidor
import io.ktor.server.engine.*
import io.ktor.server.netty.*
//importa la configuración y utilidades de Ktor
import io.ktor.server.application.*
//definir rutas HTTP y WebSocket
import io.ktor.server.routing.*
//WebSocket en el servidor
import io.ktor.server.websocket.*
import io.ktor.websocket.*
//manejar tiempos de ping y timeout
import java.time.Duration

fun main() {
    //crea y configura el servidor en el puerto 8080 y escucha a todos los clientes
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        //habilita el soporte para WebSock8899-ets en el servidor
        install(WebSockets) {
            //envia un ping para mantener la conexión activa
            pingPeriod = Duration.ofSeconds(15) // Ping cada 15 seg
            //tiempo máximo de inactividad para cerrar la conexión
            timeout = Duration.ofSeconds(30)    // Timeout si no responde
            //amaño máximo de datos que se pueden enviar/recibir en un mensaje
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        //define las rutas que maneja el servidor
        routing {
            //almacena las sesiones de los clientes conectados
            val clients = mutableMapOf<DefaultWebSocketServerSession,String>()

            //define una ruta websocket
            webSocket("/chat") { // Ruta del WS
                //println("Cliente conectado")
                var clientType = "Desconocido"

                clients[this] = clientType

                try {
                    //escucha mensajes entrantes desde el cliente
                    for (frame in incoming) {
                        //si el mensaje es un texto
                        if (frame is Frame.Text) {
                            val receivedText = frame.readText()
                            // Si es mensaje de identificación
                            if (receivedText.startsWith("IDENTIFY:")) {
                                clientType = receivedText.removePrefix("IDENTIFY:")
                                clients[this] = clientType
                                println("$clientType conectado")
                                //continue

                            }else{
                                println("Mensaje de $clientType: $receivedText")

                                // Reenviar a todos los clientes conectados
                                clients.forEach { client ->
                                    //evita enviarse mensaje a sí mismo
                                    if (client.key != this) {
                                        // Si es el saludo, mándalo con prefijo
                                        if (receivedText == "Hola Automotive") {
                                            client.key.send("$clientType dice: $receivedText")
                                        } else {
                                            // Si es JSON, mándalo puro
                                            client.key.send(receivedText)
                                        }
                                    }
                                }
                            }

                        }
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                } finally {
                    //cuando el cliente se desconecta
                    println("$clientType desconectado")
                    //quita al cliente de la lista de conexiones
                    clients.remove(this)
                }
            }
        }
    }   //inicia el servidor y espera indefinidamente
        .start(wait = true)
}