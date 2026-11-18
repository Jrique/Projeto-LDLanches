package br.com.cardapio.app.telas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.com.cardapio.app.api.ApiProvider
import br.com.cardapio.app.api.PainelApi
import br.com.cardapio.app.data.Produto
import br.com.cardapio.shared.ProdutoDto
import br.com.cardapio.shared.SalvarProdutoDto
import kotlin.math.roundToLong

class ProdutosViewModel(
    private val api: PainelApi = ApiProvider.painelApi
) {
    var carregando by mutableStateOf(false)
        private set

    /** Mensagem de erro para o Snackbar. */
    var erro by mutableStateOf<String?>(null)
        private set

    var produtos by mutableStateOf<List<Produto>>(emptyList())
        private set

    suspend fun load() = executar("Erro ao carregar produtos") {
        produtos = api.listarProdutos().map { it.toProduto() }
    }

    suspend fun criar(produto: Produto, onSuccess: () -> Unit) = executar("Erro ao criar produto") {
        api.criarProduto(produto.toDto())
        produtos = api.listarProdutos().map { it.toProduto() }
        onSuccess()
    }

    suspend fun atualizar(produto: Produto, onSuccess: () -> Unit) {
        val id = produto.id ?: return
        executar("Erro ao atualizar produto") {
            api.atualizarProduto(id, produto.toDto())
            produtos = api.listarProdutos().map { it.toProduto() }
            onSuccess()
        }
    }

    suspend fun deletar(id: Long) = executar("Erro ao excluir produto") {
        api.removerProduto(id)
        produtos = produtos.filterNot { it.id == id }
    }

    suspend fun toggleDestaque(produto: Produto) {
        val id = produto.id ?: return
        executar("Erro ao alterar destaque") {
            val atualizado = api.marcarDestaque(id, !produto.destacado).toProduto()
            produtos = produtos.map { if (it.id == id) atualizado else it }
        }
    }

    private suspend fun executar(mensagemPadrao: String, bloco: suspend () -> Unit) {
        try {
            carregando = true
            erro = null
            bloco()
        } catch (e: Throwable) {
            erro = e.message ?: mensagemPadrao
        } finally {
            carregando = false
        }
    }
}

// ================= MAPEAMENTO DTO <-> UI =================

private fun ProdutoDto.toProduto() = Produto(
    id = id,
    nome = nome,
    descricao = descricao,
    preco = precoCentavos / 100.0,
    tipo = categoriaNome.orEmpty(),
    categoriaId = categoriaId,
    adicionais = adicionais,
    destacado = destaque,
    ativo = ativo,
)

fun Produto.toDto() = SalvarProdutoDto(
    categoriaId = categoriaId,
    nome = nome,
    descricao = descricao,
    precoCentavos = (preco * 100).roundToLong(),
    adicionais = adicionais,
    destaque = destacado,
    ativo = ativo,
)
