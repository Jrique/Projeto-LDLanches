package org.example.project.telas.componentes.tela.telaConfiguracoes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardAcoes(
    onAlterarSenhaClick: () -> Unit,
    onCadastrarAdminClick: () -> Unit,
    onGerarRelatorioClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE9E6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ações", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
            BotaoAcao(
                texto = "Editar meu perfil",
                icone = Icons.Default.Edit,
                onClick = onAlterarSenhaClick
            )
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
            BotaoAcao(
                texto = "Cadastrar um novo admin",
                icone = Icons.Default.AdminPanelSettings,
                onClick = onCadastrarAdminClick
            )
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
            BotaoAcao(
                texto = "Gerar relatório",
                icone = Icons.AutoMirrored.Filled.ReceiptLong,
                onClick = onGerarRelatorioClick
            )
        }
    }
}

@Composable
private fun BotaoAcao(texto: String, icone: ImageVector, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icone, contentDescription = null, tint = Color(0xFF790001))
            Spacer(modifier = Modifier.width(16.dp))
            Text(texto, color = Color.Black, fontSize = 16.sp)
        }
    }
}