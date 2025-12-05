package org.example.project.telas.componentes.tela.telaVendas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.example.project.WindowSize
import org.example.project.rememberWindowSize
import org.example.project.telas.PedidosViewModel
import org.example.project.ui.componentesUi.ListaPedidosVendasAndroidUI
import org.example.project.ui.componentesUi.ListaPedidosVendasDesktopUI

@Composable
fun ListaPedidosVendas(
    modifier: Modifier = Modifier,
    pedidoInicialSelecionadoId: Long? = null,
    onPedidoIdConsumido: () -> Unit = {},
    viewModelExterno: PedidosViewModel = remember { PedidosViewModel() }
) {
    val windowSize = rememberWindowSize()
    val vm = remember { PedidosViewModel() }

    val pedidos = vm.pedidos
    val carregando = vm.carregando.value
    val erro = vm.erro.value

    val isMobile = windowSize == WindowSize.COMPACT

    if (isMobile) {
        ListaPedidosVendasAndroidUI(
            pedidos = pedidos,
            carregando = carregando,
            erro = erro,
            onChangeStatus = { pedido, novoStatus -> vm.alterarStatus(pedido, novoStatus) },
            onPrint = { pedido ->
                println("Imprimir pedido ${pedido.codigo}")
            },

            pedidoInicialSelecionadoId = pedidoInicialSelecionadoId,
            onPedidoIdConsumido = onPedidoIdConsumido,
            modifier = modifier
        )
    } else {
        ListaPedidosVendasDesktopUI(
            pedidos = pedidos,
            carregando = carregando,
            erro = erro,
            onChangeStatus = { pedido, novoStatus -> vm.alterarStatus(pedido, novoStatus) },
            onPrint = { pedido ->
                println("Imprimir pedido ${pedido.codigo}")
            },
            pedidoInicialSelecionadoId = pedidoInicialSelecionadoId,
            onPedidoIdConsumido = onPedidoIdConsumido,
            modifier = modifier
        )
    }
}