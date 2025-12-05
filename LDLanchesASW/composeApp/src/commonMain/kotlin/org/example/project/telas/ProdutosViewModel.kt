package org.example.project.telas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.api.ApiProvider
import org.example.project.api.LanchesApi
import org.example.project.api.ProductDTO
import org.example.project.data.Produto
import kotlin.Boolean

class ProdutosViewModel(
    private val api: LanchesApi = ApiProvider.lanchesApi
) {

    // se está carregando alguma operação
    var carregando by mutableStateOf(false)
        private set

    // mensagem de erro (para Snackbar)
    var erro by mutableStateOf<String?>(null)
        private set

    // lista de produtos exibidos na UI
    var produtos by mutableStateOf<List<Produto>>(emptyList())
        private set

    /** Carrega todos os produtos da API */
    suspend fun load() {
        try {
            carregando = true
            erro = null

            // busca na API (DTO)
            val listaDto = api.listarProdutos()

            // converte DTO -> Produto (modelo da UI)
            produtos = listaDto.map { it.toProduto() }

        } catch (e: Throwable) {
            erro = e.message ?: "Erro ao carregar produtos"
        } finally {
            carregando = false
        }
    }

    /** Cria um novo produto */
    suspend fun criar(produto: Produto, onSuccess: () -> Unit) {
        try {
            carregando = true
            erro = null

            api.criarProduto(produto.toDto())
            // recarrega lista depois de criar
            load()
            onSuccess()
        } catch (e: Throwable) {
            erro = e.message ?: "Erro ao criar produto"
        } finally {
            carregando = false
        }
    }

    /** Atualiza um produto existente */
    suspend fun atualizar(produto: Produto, onSuccess: () -> Unit) {
        val id = produto.id ?: return

        try {
            carregando = true
            erro = null

            api.atualizarProduto(id, produto.toDto())
            load()
            onSuccess()
        } catch (e: Throwable) {
            erro = e.message ?: "Erro ao atualizar produto"
        } finally {
            carregando = false
        }
    }

    /** Deleta um produto pelo id */
    suspend fun deletar(id: Long) {
        try {
            carregando = true
            erro = null

            api.deletarProduto(id)
            load()
        } catch (e: Throwable) {
            erro = e.message ?: "Erro ao deletar produto"
        } finally {
            carregando = false
        }
    }
    suspend fun toggleDestaque(produto: Produto) {
        val id = produto.id ?: return

        try {
            carregando = true
            erro = null

            // chama o PATCH do backend invertendo o valor atual
            val dtoAtualizado = api.setHighlight(id, !produto.destacado)
            val atualizado = dtoAtualizado.toProduto()

            // atualiza só o item na lista atual sem precisar recarregar tudo
            produtos = produtos.map { p ->
                if (p.id == id) atualizado else p
            }

        } catch (e: Throwable) {
            erro = e.message ?: "Erro ao alterar destaque"
        } finally {
            carregando = false
        }
    }
}






// ================= MAPEAMENTO DTO <-> UI =================

private fun ProductDTO.toProduto() = Produto(
    id = id,
    nome = name,
    descricao = description,
    preco = price,
    adicionais = extra,
    tipo = type,
    destacado = highlighted
)

fun Produto.toDto() = ProductDTO(
    id = id,
    name = nome,
    description = descricao,
    price = preco,
    extra = adicionais,
    type = tipo,
    highlighted = destacado
)
