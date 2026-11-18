package br.com.cardapio.app.telas

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
import br.com.cardapio.app.api.ApiProvider
import br.com.cardapio.app.api.apiBaseUrl
import br.com.cardapio.app.telas.componentes.EditorAparencia
import br.com.cardapio.shared.AparenciaDto
import br.com.cardapio.shared.LojaDto
import kotlinx.coroutines.launch

/** O dono personaliza o cardápio da loja dentro do que o super admin liberou. */
@Composable
fun TelaAparencia() {
    val api = ApiProvider.painelApi
    val escopo = rememberCoroutineScope()

    var loja by remember { mutableStateOf<LojaDto?>(null) }
    var aparencia by remember { mutableStateOf<AparenciaDto?>(null) }
    var mensagem by remember { mutableStateOf<String?>(null) }
    var salvando by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            loja = api.minhaLoja().also { aparencia = it.aparencia }
        } catch (e: Exception) {
            mensagem = e.message
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFF8F6)).verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Aparência da loja", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)

        val atual = loja
        val ap = aparencia
        if (atual == null || ap == null) {
            if (mensagem == null) CircularProgressIndicator(color = Color(0xFF790001)) else Text(mensagem!!, color = Color(0xFF790001))
            return@Column
        }

        Text("Seu cardápio online: ${apiBaseUrl()}/${atual.slug}/", color = Color.Gray)

        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE9E6))) {
            Column(Modifier.padding(16.dp)) {
                EditorAparencia(ap, liberado = { it in atual.personalizacaoLiberada }, onChange = { aparencia = it })
            }
        }

        mensagem?.let { Text(it, color = Color(0xFF790001)) }

        Button(
            onClick = {
                escopo.launch {
                    try {
                        salvando = true
                        mensagem = null
                        loja = api.personalizarLoja(ap).also { aparencia = it.aparencia }
                        mensagem = "Aparência salva. Recarregue o cardápio para ver as mudanças."
                    } catch (e: Exception) {
                        mensagem = e.message
                    } finally {
                        salvando = false
                    }
                }
            },
            enabled = !salvando,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF790001)),
        ) { Text(if (salvando) "Salvando..." else "Salvar aparência") }
    }
}
