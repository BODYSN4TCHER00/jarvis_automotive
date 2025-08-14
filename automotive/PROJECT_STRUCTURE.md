# Estructura del Proyecto - Android Automotive

## 📁 Organización de Archivos

```
automotive/src/main/java/com/example/jarvis/
├── MainActivity.kt                    # Actividad principal
├── network/                           # 🆕 Funcionalidad WebSocket
│   ├── WebSocketServer.kt            # Servidor WebSocket
│   ├── NetworkViewModel.kt           # ViewModel para red
│   └── WebSocketClient.kt            # Cliente para apps móviles
├── screens/                          # Pantallas de la aplicación
│   ├── HomeScreen.kt                 # Pantalla principal
│   ├── JobsScreen.kt                 # Pantalla de trabajos
│   ├── ToolsScreen.kt                # Pantalla de herramientas
│   ├── JobToolsScreen.kt             # Herramientas por trabajo
│   └── NetworkScreen.kt              # 🆕 Pantalla del servidor
└── components/                       # Componentes reutilizables
    ├── navigation/
    │   └── NavigationComponent.kt    # Navegación principal
    ├── ui/                           # Componentes de UI
    │   ├── ListItem.kt               # Elemento de lista
    │   ├── ToolCard.kt               # Tarjeta de herramienta
    │   ├── TopBar.kt                 # Barra superior
    │   ├── SectionTitle.kt           # Título de sección
    │   └── ScreenContainer.kt        # Contenedor de pantalla
    └── theme/
        └── Theme.kt                  # Tema de la aplicación
```

## 🎯 Funcionalidades por Módulo

### **network/** - Comunicación WebSocket
- **WebSocketServer.kt**: Servidor que acepta conexiones de dispositivos móviles
- **NetworkViewModel.kt**: Maneja la lógica de negocio del servidor
- **WebSocketClient.kt**: Cliente para usar en aplicaciones móviles

### **screens/** - Interfaces de Usuario
- **HomeScreen.kt**: Pantalla principal con navegación
- **JobsScreen.kt**: Lista de trabajos disponibles
- **ToolsScreen.kt**: Lista de herramientas
- **JobToolsScreen.kt**: Herramientas específicas por trabajo
- **NetworkScreen.kt**: Control del servidor WebSocket

### **components/** - Componentes Reutilizables
- **navigation/**: Sistema de navegación
- **ui/**: Componentes de interfaz reutilizables
- **theme/**: Configuración de tema y estilos

## 🔧 Archivos de Configuración

```
automotive/
├── build.gradle.kts                  # Dependencias del proyecto
├── AndroidManifest.xml               # Configuración de la app
├── WEBSOCKET_README.md               # Documentación WebSocket
└── PROJECT_STRUCTURE.md              # Este archivo
```

## ✅ Estado de Limpieza

- ✅ **Sin archivos duplicados**
- ✅ **Nombres claros y descriptivos**
- ✅ **Estructura lógica y organizada**
- ✅ **Separación de responsabilidades**
- ✅ **Documentación actualizada**

## 🚀 Próximos Pasos

1. **Compilar** el proyecto
2. **Ejecutar** en el emulador
3. **Probar** la funcionalidad WebSocket
4. **Desarrollar** aplicación móvil cliente

## 📝 Notas de Desarrollo

- Todos los archivos están en uso y referenciados correctamente
- No hay código obsoleto o sin usar
- La estructura sigue las mejores prácticas de Android
- El código está optimizado y limpio 