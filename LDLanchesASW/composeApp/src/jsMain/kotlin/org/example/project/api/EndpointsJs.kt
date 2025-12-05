package org.example.project.api

import kotlinx.browser.window

actual fun apiBaseUrl(): String {
    // Usa o host onde o app Web está rodando e força porta do Spring Boot
    // Ex.: se o app Web (DevServer) estiver em localhost, isso vira http://localhost:8081
    val host = window.location.hostname // "localhost" na sua máquina
    return "http://$host:8081"
}
