package org.example.project.data

data class Produto(
    val id: Long? = null,
    val nome: String,
    val descricao: String = "",
    val preco: Double,
    val tipo: String = "",
    val adicionais: String = "",
    val destacado: Boolean = false
)
