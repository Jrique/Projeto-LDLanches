package org.example.project.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.getPlatform
import org.example.project.telas.componentes.tela.telaMonitoramento.ModalConfigurarAlertas

@Composable
fun TelaMonitoramento() {
    val vm = viewModel { MonitoramentoViewModel() }
    val isMobile = getPlatform().name.contains("Android")
    var mostrarConfig by remember { mutableStateOf(false) }

    // Lógica Dinâmica com 4 estados
    val (corStatus, textoStatus, iconeStatus) = when {
        // Muito quente (Crítico) -> Vermelho
        vm.temperatura >= vm.limiteCritico -> Triple(Color(0xFFD32F2F), "CRÍTICO: Temp. Alta", Icons.Default.Warning)

        // Quase quente (Alerta) -> Laranja

        // Muito Frio -> Azul
        vm.temperatura <= vm.limiteMuitoFrio -> Triple(Color(0xFF2196F3), "MUITO FRIO", Icons.Default.AcUnit)

        // Normal -> Verde
        else -> Triple(Color(0xFF2E7D32), "Normal", Icons.Default.AcUnit)
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Monitoramento Freezer",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = if (isMobile) 28.sp else 40.sp,
                color = Color(0xFF4E342E),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Card Principal (Termômetro)
            val tamanhoCirculo = if (isMobile) 240.dp else 320.dp

            Card(
                elevation = CardDefaults.cardElevation(8.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.size(tamanhoCirculo)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.85f)
                            .border(width = if(isMobile) 6.dp else 10.dp, color = corStatus.copy(alpha = 0.3f), shape = CircleShape)
                            .background(corStatus.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = iconeStatus,
                                contentDescription = null,
                                tint = corStatus,
                                modifier = Modifier.size(if(isMobile) 40.dp else 56.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${vm.temperatura}°C",
                                fontSize = if(isMobile) 48.sp else 72.sp,
                                fontWeight = FontWeight.Bold,
                                color = corStatus
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // STATUS + CONFIGURAÇÃO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = corStatus),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = textoStatus,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                        fontSize = if(isMobile) 18.sp else 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { mostrarConfig = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4E342E)
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Configurar")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Última atualização: ${vm.horario}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                // Exibe os 3 limites configurados
                Text(
                    text = "Frio (${vm.limiteMuitoFrio}) | Alerta (${vm.limiteAlerta}) | Crítico (${vm.limiteCritico})",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (vm.erro != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = vm.erro ?: "",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (mostrarConfig) {
        ModalConfigurarAlertas(
            atualAlerta = vm.limiteAlerta,
            atualCritico = vm.limiteCritico,
            atualMuitoFrio = vm.limiteMuitoFrio,
            onDismiss = { mostrarConfig = false },
            onSalvar = { novoAlerta, novoCritico, novoFrio ->
                vm.atualizarLimites(novoAlerta, novoCritico, novoFrio)
                mostrarConfig = false
            }
        )
    }
}