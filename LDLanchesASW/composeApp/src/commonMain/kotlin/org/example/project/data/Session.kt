package org.example.project.data


object Session {
    var usuarioLogado: UsuarioSessao? = null
}

data class UsuarioSessao(
    val id: Long,
    val nome: String,
    val email: String,
    val token: String
)