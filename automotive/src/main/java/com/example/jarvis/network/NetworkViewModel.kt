package com.example.jarvis.network

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.NetworkInterface
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class NetworkViewModel(application: Application) : AndroidViewModel(application) {
    
    private val TAG = "NetworkViewModel"
    private var webSocketServer: WebSocketServer? = null
    
    // Estados del UI
    private val _isServerRunning = MutableStateFlow(false)
    val isServerRunning: StateFlow<Boolean> = _isServerRunning.asStateFlow()
    
    private val _isClientConnected = MutableStateFlow(false)
    val isClientConnected: StateFlow<Boolean> = _isClientConnected.asStateFlow()
    
    private val _serverIp = MutableStateFlow<String?>(null)
    val serverIp: StateFlow<String?> = _serverIp.asStateFlow()
    
    private val _statusMessage = MutableStateFlow("Servidor no iniciado")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    fun startServer() {
        if (webSocketServer != null && _isServerRunning.value) {
            _statusMessage.value = "El servidor ya está ejecutándose"
            return
        }

        viewModelScope.launch {
            try {
                _statusMessage.value = "Iniciando servidor..."
                
                webSocketServer = WebSocketServer(
                    port = 3000,
                    onStatusChanged = { status ->
                        _statusMessage.value = status
                        _isClientConnected.value = webSocketServer?.isClientConnected() ?: false
                    }
                )

                webSocketServer?.startServer()
                _isServerRunning.value = true
                
                // Obtener IP después de iniciar el servidor
                val ip = getLocalIpAddress()
                _serverIp.value = ip
                
                if (ip != null) {
                    Log.d(TAG, "Servidor iniciado en $ip:3000")
                    _statusMessage.value = "Servidor iniciado en $ip:3000 - Esperando conexiones..."
                } else {
                    Log.w(TAG, "Servidor iniciado pero no se pudo obtener IP")
                    _statusMessage.value = "Servidor iniciado en puerto 3000 - IP no disponible"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error iniciando servidor: ${e.message}")
                _statusMessage.value = "Error iniciando servidor: ${e.message}"
                _isServerRunning.value = false
                webSocketServer = null
            }
        }
    }

    fun stopServer() {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Deteniendo servidor..."
                webSocketServer?.stopServer()
                webSocketServer = null
                _isServerRunning.value = false
                _isClientConnected.value = false
                _serverIp.value = null
                _statusMessage.value = "Servidor detenido"
                Log.d(TAG, "Servidor detenido")
            } catch (e: Exception) {
                Log.e(TAG, "Error deteniendo servidor: ${e.message}")
                _statusMessage.value = "Error deteniendo servidor: ${e.message}"
            }
        }
    }

    fun sendTestMessage() {
        webSocketServer?.sendMessage("Mensaje de prueba desde Automotive - ${System.currentTimeMillis()}")
    }

    private fun getLocalIpAddress(): String? {
        // Para emuladores, usar la IP del host
        if (isEmulator()) {
            val hostIp = getHostIpAddress()
            Log.d(TAG, "Emulador detectado, usando IP del host: $hostIp")
            return hostIp
        }

        // Para dispositivos físicos, obtener IP de WiFi
        try {
            val wifiManager = getApplication<Application>().getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = wifiInfo.ipAddress
            
            if (ipAddress != 0) {
                val ip = String.format(
                    Locale.US,
                    "%d.%d.%d.%d",
                    ipAddress and 0xff,
                    ipAddress shr 8 and 0xff,
                    ipAddress shr 16 and 0xff,
                    ipAddress shr 24 and 0xff
                )
                Log.d(TAG, "IP obtenida de WiFi: $ip")
                return ip
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo IP WiFi: ${e.message}")
        }

        // Fallback: obtener IP de cualquier interfaz de red
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                
                // Saltar interfaces loopback y down
                if (networkInterface.isLoopback || !networkInterface.isUp) {
                    continue
                }
                
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    
                    // Solo IPv4 y no loopback
                    if (!inetAddress.isLoopbackAddress && 
                        inetAddress.hostAddress.indexOf(':') < 0 &&
                        inetAddress.hostAddress.startsWith("192.168.")) {
                        
                        Log.d(TAG, "IP obtenida de interfaz ${networkInterface.displayName}: ${inetAddress.hostAddress}")
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo IP de red: ${e.message}")
        }

        // Último intento: obtener cualquier IP válida
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                if (networkInterface.isLoopback || !networkInterface.isUp) {
                    continue
                }
                
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress.hostAddress.indexOf(':') < 0) {
                        Log.d(TAG, "IP obtenida (fallback): ${inetAddress.hostAddress}")
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en fallback de IP: ${e.message}")
        }

        return null
    }

    private fun isEmulator(): Boolean {
        return try {
            // Método más robusto para detectar emulador
            val buildConfig = Class.forName("android.os.Build")
            val field = buildConfig.getField("FINGERPRINT")
            val fingerprint = field.get(null) as String
            
            val isEmulator = fingerprint.contains("generic") || 
                             fingerprint.contains("sdk") || 
                             fingerprint.contains("emulator") ||
                             fingerprint.contains("google_sdk") ||
                             fingerprint.contains("vbox86p") ||
                             fingerprint.contains("goldfish")
            
            Log.d(TAG, "🔍 Detección de emulador: fingerprint=$fingerprint, isEmulator=$isEmulator")
            isEmulator
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error detectando emulador: ${e.message}")
            // Si no se puede detectar, asumir que es emulador por seguridad
            true
        }
    }

    private fun getHostIpAddress(): String? {
        return try {
            Log.d(TAG, "🔍 Obteniendo IP del host...")
            
            // Intentar obtener la IP del host desde diferentes interfaces
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                
                // Buscar interfaces que no sean loopback y estén activas
                if (!networkInterface.isLoopback && networkInterface.isUp) {
                    Log.d(TAG, "📡 Interfaz encontrada: ${networkInterface.displayName}")
                    
                    val inetAddresses = networkInterface.inetAddresses
                    while (inetAddresses.hasMoreElements()) {
                        val inetAddress = inetAddresses.nextElement()
                        
                        // Buscar IPs que sean accesibles desde la red
                        if (!inetAddress.isLoopbackAddress && 
                            inetAddress.hostAddress?.indexOf(':') ?: -1 < 0 &&
                            (inetAddress.hostAddress?.startsWith("192.168.") == true || 
                             inetAddress.hostAddress?.startsWith("10.0.") == true ||
                             inetAddress.hostAddress?.startsWith("172.") == true)) {
                            
                            val ip = inetAddress.hostAddress
                            Log.d(TAG, "🌐 IP del host encontrada: $ip")
                            return ip
                        }
                    }
                }
            }
            
            // Si no se encuentra, usar una IP común para desarrollo
            Log.w(TAG, "⚠️ No se pudo obtener IP del host, usando IP por defecto")
            "192.168.1.100"
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error obteniendo IP del host: ${e.message}")
            "192.168.1.100"
        }
    }

    fun getServerInfo(): String {
        return if (_serverIp.value != null) {
            "ws://${_serverIp.value}:3000"
        } else {
            "Servidor no disponible"
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketServer?.stopServer()
    }
} 