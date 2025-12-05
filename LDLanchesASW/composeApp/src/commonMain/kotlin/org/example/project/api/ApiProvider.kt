package org.example.project.api

import io.ktor.client.HttpClient

object ApiProvider {
    val httpClient: HttpClient by lazy { createHttpClient() }
    val lanchesApi: LanchesApi by lazy { LanchesApi(httpClient) }
}