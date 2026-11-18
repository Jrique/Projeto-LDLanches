package br.com.cardapio.app.telas.componentes.tela.telaConfiguracoes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ldlanchesasw.composeapp.generated.resources.Res
import ldlanchesasw.composeapp.generated.resources.logo
import br.com.cardapio.app.data.Session
import org.jetbrains.compose.resources.painterResource

@Composable
fun CardPerfil() {
    val usuario = Session.usuarioLogado
    val nomeExibicao = usuario?.nome ?: "Usuário"
    val emailExibicao = usuario?.email ?: "Não logado"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE9E6))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(nomeExibicao, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(emailExibicao, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                Text(
                    listOfNotNull(usuario?.papel?.rotulo, usuario?.lojaSlug?.let { "loja: $it" }).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF790001)
                )
            }
        }
    }
}