package br.com.cardapio.server.dominio

import java.time.Instant

// Contratos de persistência. O domínio define o que precisa; a infra implementa.
// Todo método de dados de loja recebe o lojaId — é isso que garante o isolamento entre lojas.

interface RepositorioLojas {
    fun buscar(id: Long): Loja?
    fun buscarPorSlug(slug: String): Loja?
    fun listar(): List<Loja>
    fun existeSlug(slug: String, exceto: Long? = null): Boolean
    fun salvar(loja: Loja): Loja
}

interface RepositorioUsuarios {
    fun buscar(id: Long): Usuario?
    fun buscarDaLoja(lojaId: Long, id: Long): Usuario?
    fun buscarPorEmail(email: String): Usuario?
    fun listarDaLoja(lojaId: Long): List<Usuario>
    fun existeEmail(email: String, exceto: Long? = null): Boolean
    fun existeSuperAdmin(): Boolean
    fun salvar(usuario: Usuario): Usuario
    fun remover(lojaId: Long, id: Long)
}

interface RepositorioCatalogo {
    fun listarCategorias(lojaId: Long): List<Categoria>
    fun buscarCategoria(lojaId: Long, id: Long): Categoria?
    fun existeCategoriaComNome(lojaId: Long, nome: String, exceto: Long? = null): Boolean
    fun salvarCategoria(categoria: Categoria): Categoria
    fun removerCategoria(lojaId: Long, id: Long)

    fun listarProdutos(lojaId: Long, somenteAtivos: Boolean = false): List<Produto>
    fun buscarProduto(lojaId: Long, id: Long): Produto?
    fun salvarProduto(produto: Produto): Produto
    fun removerProduto(lojaId: Long, id: Long)
}

interface RepositorioPedidos {
    fun buscar(lojaId: Long, id: Long): Pedido?
    fun buscarPorCodigo(lojaId: Long, codigo: String): Pedido?
    fun existeCodigo(lojaId: Long, codigo: String): Boolean
    fun listar(lojaId: Long, de: Instant, ate: Instant, limite: Int): List<Pedido>
    fun salvar(pedido: Pedido): Pedido
}

/** Executa um bloco dentro de uma transação de banco. */
interface Transacoes {
    suspend fun <T> executar(bloco: () -> T): T
}
