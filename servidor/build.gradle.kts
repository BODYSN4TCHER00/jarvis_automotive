plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm) // Use the version from libs.versions.toml
    application
}

/*repositories {
    mavenCentral()
}*/

dependencies {
    //manejo de routing, sesiones, configuración
    implementation("io.ktor:ktor-server-core:2.3.4")
    //maneja las conexiones de red bajo Ktor
    implementation("io.ktor:ktor-server-netty:2.3.4")
    //WebSockets para comunicación bidireccional en tiempo real
    implementation("io.ktor:ktor-server-websockets:2.3.4")
    //para ver logs en consola
    implementation("ch.qos.logback:logback-classic:1.4.11")
}

application {
    //clase principal donde se va a iniciar el servidor
    mainClass.set("Servidor") // nombre del archivo donde pondrás fun main()
}