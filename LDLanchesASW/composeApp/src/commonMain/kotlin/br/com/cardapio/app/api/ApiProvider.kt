package br.com.cardapio.app.api

import io.ktor.client.HttpClient

object ApiProvider {
    val httpClient: HttpClient by lazy { createHttpClient() }
    val painelApi: PainelApi by lazy { PainelApi(httpClient) }
}
