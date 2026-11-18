package br.com.cardapio.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform