package org.example.project.telas

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.api.ApiProvider
import org.example.project.api.LanchesApi
import org.example.project.api.OrderDTO
import org.example.project.api.ProductDTO
import org.example.project.api.createHttpClient
import org.example.project.data.Pedido
import kotlin.math.roundToLong

fun Double.toReal(): String {
    val totalCentavos = (this * 100).roundToLong()
    val reais = totalCentavos / 100
    val centavos = totalCentavos % 100
    val centavosString = centavos.toString().padStart(2, '0')

    return "R$ $reais,$centavosString"
}

class PedidosViewModel : ViewModel() {

    private val api: LanchesApi = ApiProvider.lanchesApi

    val carregando = mutableStateOf(false)
    val erro = mutableStateOf<String?>(null)
    val pedidos = mutableStateListOf<Pedido>()

    init {
        carregar()
    }

    fun carregar(inicio: String? = null, fim: String? = null) {
        viewModelScope.launch {
            try {
                carregando.value = true
                erro.value = null

                val lista = api.listarPedidos(inicio, fim)
                val ordenada = lista.sortedByDescending { it.id ?: 0L }

                pedidos.clear()
                pedidos.addAll(ordenada.map { it.toPedido() })

            } catch (e: Throwable) {
                erro.value = e.message
            } finally {
                carregando.value = false
            }
        }
    }



    fun alterarStatus(pedido: Pedido, novoStatusLabel: String) {
        viewModelScope.launch {
            try {
                carregando.value = true
                erro.value = null

                val statusCode = labelToCode(novoStatusLabel)

                // backend devolve o OrderDTO já com status/data atualizados
                val dtoAtualizado = api.atualizarStatusPedido(pedido.id, statusCode)

                // procura esse pedido na lista atual
                val idx = pedidos.indexOfFirst { it.id == pedido.id }

                if (idx != -1) {
                    // converte o DTO atualizado em Pedido
                    val label = codeToLabel(dtoAtualizado.status)
                    val pedidoAtualizado = Pedido(
                        id = dtoAtualizado.id ?: pedido.id,
                        codigo = dtoAtualizado.code,
                        descricao = dtoAtualizado.description,
                        valor = dtoAtualizado.total.toReal(),
                        horario = dtoAtualizado.date,
                        status = label,
                        corStatus = codeToColor(dtoAtualizado.status)
                    )

                    // remove da posição antiga e coloca no topo
                    pedidos.removeAt(idx)
                    pedidos.add(0, pedidoAtualizado)
                } else {
                    // se não achar por algum motivo, recarrega tudo
                    carregar()
                }

            } catch (e: Throwable) {
                erro.value = e.message
            } finally {
                carregando.value = false
            }
        }
    }


    private fun OrderDTO.toPedido(): Pedido {
        val label = codeToLabel(status)
        return Pedido(
            id = id ?: 0L,
            codigo = code,                 // primeira coluna da tabela
            descricao = description,       // detalhes do pedido
            valor = total.toReal(),
            horario = date,
            status = label,
            corStatus = codeToColor(status)
        )
    }

    private fun codeToLabel(statusCode: String): String = when (statusCode.uppercase()) {
        "NOVO" -> "Novo"
        "VISTO" -> "Visto"
        "EM_PREPARO" -> "Em preparo"
        "SAIU_PARA_ENTREGA" -> "Saiu para entrega"
        "FINALIZADO" -> "Finalizado"
        else -> statusCode
    }

    private fun labelToCode(label: String): String = when (label.lowercase()) {
        "novo" -> "NOVO"
        "visto" -> "VISTO"
        "em preparo" -> "EM_PREPARO"
        "saiu para entrega" -> "SAIU_PARA_ENTREGA"
        "finalizado" -> "FINALIZADO"
        else -> label.uppercase()
    }

    private fun codeToColor(statusCode: String): Color = when (statusCode.uppercase()) {
        "NOVO" -> Color(0xFF2196F3)
        "VISTO" -> Color(0xFF4CAF50)
        "EM_PREPARO" -> Color(0xFFFFC107)
        "SAIU_PARA_ENTREGA" -> Color(0xFF9C27B0)
        "FINALIZADO" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}


