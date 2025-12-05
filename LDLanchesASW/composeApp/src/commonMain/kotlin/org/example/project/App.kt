package org.example.project

import androidx.compose.runtime.*
import org.example.project.telas.TelaLogin
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
 var usuarioLogado by remember { mutableStateOf(false) }
 var telaAtual by remember { mutableStateOf(Tela.DASHBOARD) }

 if (!usuarioLogado) {
     TelaLogin(
         onLoginSuccess = {
             usuarioLogado = true
         }
     )
 } else {
     val onTelaSelecionada: (Tela) -> Unit = { novaTela ->
         telaAtual = novaTela
     }
     val onLogout: () -> Unit = {
         usuarioLogado = false
         telaAtual = Tela.DASHBOARD
     }

     val windowSize = rememberWindowSize()

     if (windowSize == WindowSize.COMPACT) {
         LayoutEstiloAndroid(telaAtual, onTelaSelecionada, onLogout)
     } else {
         LayoutEstiloDesktop(telaAtual, onTelaSelecionada, onLogout)
     }
 }
}

