# Servidor WebSocket - Android Automotive

Servidor WebSocket simple para comunicación entre tu aplicación Android Automotive y tu dispositivo móvil.

## 📁 Estructura del Proyecto

```
automotive/src/main/java/com/example/jarvis/
├── network/
│   ├── WebSocketServer.kt      # Servidor WebSocket
│   ├── NetworkViewModel.kt     # Lógica de la aplicación
│   └── WebSocketClient.kt      # Cliente para aplicación móvil
├── screens/
│   └── NetworkScreen.kt        # Interfaz de usuario
└── components/navigation/
    └── NavigationComponent.kt  # Navegación (actualizado)
```

## 🎯 Características

- ✅ **Servidor WebSocket** en puerto 8080
- ✅ **Un solo cliente** (tu dispositivo móvil)
- ✅ **Interfaz simple** con solo 3 botones
- ✅ **Mensaje predefinido**: "Conexión a Automotive exitosa"
- ✅ **Detección automática de IP**

## 📱 Cómo Usar

### 1. En la Aplicación Automotive

1. Ejecuta la aplicación en el emulador
2. Ve a "Servidor WebSocket" desde la pantalla principal
3. Presiona **"Iniciar"** para activar el servidor
4. Anota la IP mostrada (ej: `192.168.1.100:8080`)
5. Espera a que se conecte tu dispositivo móvil
6. Presiona **"Enviar Mensaje de Prueba"** para enviar el mensaje

### 2. En tu Aplicación Móvil

#### Dependencias (build.gradle)
```gradle
implementation 'org.java-websocket:Java-WebSocket:1.5.3'
```

#### Permisos (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

#### Código de Conexión
```kotlin
private lateinit var webSocketClient: WebSocketClient

private fun connectToAutomotive() {
    // Reemplaza con la IP mostrada en Automotive
    val serverUrl = "ws://192.168.1.100:8080"
    
    webSocketClient = WebSocketClient(
        serverUrl = serverUrl,
        onMessageReceived = { message ->
            // Mostrar mensaje recibido
            Log.d("MainActivity", "Mensaje: $message")
            runOnUiThread {
                showMessage(message)
            }
        },
        onConnectionEstablished = {
            Log.d("MainActivity", "Conectado a Automotive")
            runOnUiThread {
                showConnectionStatus("Conectado")
            }
        },
        onConnectionClosed = {
            Log.d("MainActivity", "Desconectado de Automotive")
            runOnUiThread {
                showConnectionStatus("Desconectado")
            }
        }
    )
    
    webSocketClient.connect()
}

private fun sendMessage(message: String) {
    webSocketClient.sendMessage(message)
}

override fun onDestroy() {
    super.onDestroy()
    webSocketClient.close()
}
```

## 🎨 Estados de la Interfaz

- **🔴 Rojo**: Servidor inactivo
- **🟡 Amarillo**: Servidor activo, esperando conexión
- **🟢 Verde**: Cliente conectado

## 📋 Flujo de Comunicación

1. **Automotive inicia servidor** → Muestra IP
2. **Móvil se conecta** → Estado cambia a verde
3. **Automotive envía mensaje** → Móvil recibe "Conexión a Automotive exitosa"
4. **Móvil puede enviar mensajes** → Automotive los recibe y muestra

## 🔧 Solución de Problemas

### No se conecta
- Verifica que ambos dispositivos estén en la misma red WiFi
- Confirma la IP mostrada en Automotive
- Asegúrate de que el puerto 8080 no esté bloqueado

### IP no aparece
- Verifica la conexión WiFi en el emulador
- Reinicia la aplicación Automotive

## 🚀 Próximos Pasos

1. Compila y ejecuta la aplicación Automotive
2. Inicia el servidor
3. Crea una aplicación móvil simple con el código de ejemplo
4. ¡Prueba la comunicación!

## 📝 Notas

- El servidor acepta solo un cliente a la vez
- Los mensajes se envían como texto plano
- La conexión se mantiene activa hasta que se detenga el servidor 