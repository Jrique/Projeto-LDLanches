package org.example.project.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.getPlatform
import org.example.project.telas.componentes.tela.telaConfiguracoes.*

@Composable
fun TelaConfiguracoes() {
    val scrollState = rememberScrollState()
    val isMobile = getPlatform().name.contains("Android")

    var mostrarModalSenha by remember { mutableStateOf(false) }
    var mostrarModalAdmin by remember { mutableStateOf(false) } // Este estado agora controla o CRUD de Admins
    var mostrarModalRelatorio by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F6))
            .verticalScroll(scrollState)
            .padding(if (isMobile) 16.dp else 18.dp)
    ) {
        Text(
            text = "Configurações",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = if (isMobile) 32.sp else 50.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        CardPerfil()
        Spacer(modifier = Modifier.height(24.dp))
        CardAcoes(
            onAlterarSenhaClick = { mostrarModalSenha = true },
            onCadastrarAdminClick = { mostrarModalAdmin = true }, // Renomeado para mais clareza
            onGerarRelatorioClick = { mostrarModalRelatorio = true }
        )
    }

    if (mostrarModalSenha) {
        ModalAlterarSenha(onDismiss = { mostrarModalSenha = false })
    }
    // A chamada agora é para o novo modal de gerenciamento
    if (mostrarModalAdmin) {
        ModalGerenciarAdmins(onDismiss = { mostrarModalAdmin = false })
    }
    if (mostrarModalRelatorio) {
        ModalGerarRelatorio(onDismiss = { mostrarModalRelatorio = false })
    }
}