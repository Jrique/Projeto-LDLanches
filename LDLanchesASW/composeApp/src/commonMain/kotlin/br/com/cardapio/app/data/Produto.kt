package br.com.cardapio.app.data

data class Produto(
    val id: Long? = null,
    val nome: String,
    val descricao: String = "",
    val preco: Double,
    /** Nome da categoria (exibição). */
    val tipo: String = "",
    val categoriaId: Long? = null,
    val adicionais: String = "",
    val destacado: Boolean = false,
    val ativo: Boolean = true,
)
