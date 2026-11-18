package br.com.cardapio.app.telas.componentes.tela.telaVendas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import br.com.cardapio.app.WindowSize
import br.com.cardapio.app.rememberWindowSize
import br.com.cardapio.app.telas.PedidosViewModel
import br.com.cardapio.app.ui.componentesUi.ListaPedidosVendasAndroidUI
import br.com.cardapio.app.ui.componentesUi.ListaPedidosVendasDesktopUI

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