package br.com.cardapio.app

import androidx.compose.runtime.*
import br.com.cardapio.app.data.Session
import br.com.cardapio.app.telas.TelaLogin
import br.com.cardapio.app.telas.TelaLojas
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val usuario = Session.usuarioLogado

    if (usuario == null) {
        // Session é estado do Compose: ao logar, esta função recompõe sozinha.
        TelaLogin(onLoginSuccess = {})
        return
    }

    if (usuario.ehSuperAdmin) {
        TelaLojas(onLogout = Session::encerrar)
        return
    }

    var telaAtual by remember(usuario.id) { mutableStateOf(Tela.inicialPara(usuario)) }
    val onTelaSelecionada: (Tela) -> Unit = { novaTela -> telaAtual = novaTela }
    val onLogout: () -> Unit = { Session.encerrar() }

    if (rememberWindowSize() == WindowSize.COMPACT) {
        LayoutEstiloAndroid(telaAtual, onTelaSelecionada, onLogout)
    } else {
        LayoutEstiloDesktop(telaAtual, onTelaSelecionada, onLogout)
    }
}
