package org.example.project.telas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.example.project.api.ApiProvider
import org.example.project.api.LanchesApi
import org.example.project.api.toDomain
import org.example.project.data.Categoria

class CategoriasViewModel(
    private val api: LanchesApi = ApiProvider.lanchesApi
) {

    var categorias by mutableStateOf<List<Categoria>>(emptyList())
        private set

    var carregando by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    suspend fun carregar() {
        try {
            carregando = true
            erro = null
            val dtos = api.listarCategorias()
            categorias = dtos.map { it.toDomain() }
        } catch (e: Exception) {
            erro = e.message
        } finally {
            carregando = false
        }
    }

    suspend fun salvar(nome: String, idExistente: Long? = null) {
        if (nome.isBlank()) return

        val idValido = idExistente?.takeIf { it > 0 }

        if (idValido == null) {
            val dto = api.criarCategoria(nome)
            categorias = categorias + dto.toDomain()
        } else {
            val dto = api.atualizarCategoria(idValido, nome)
            categorias = categorias.map {
                if (it.id == idValido) dto.toDomain() else it
            }
        }
    }

    suspend fun excluir(id: Long) {
        api.deletarCategoria(id)
        categorias = categorias.filterNot { it.id == id }
    }
}
