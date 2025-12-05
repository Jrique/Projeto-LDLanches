package org.example.project.telas.componentes.tela.telaMonitoramento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ModalConfigurarAlertas(
    atualAlerta: Double,
    atualCritico: Double,
    atualMuitoFrio: Double, // <--- Novo parâmetro
    onDismiss: () -> Unit,
    onSalvar: (Double, Double, Double) -> Unit // <--- Recebe 3 valores agora
) {
    var txtAlerta by remember { mutableStateOf(atualAlerta.toString()) }
    var txtCritico by remember { mutableStateOf(atualCritico.toString()) }
    var txtMuitoFrio by remember { mutableStateOf(atualMuitoFrio.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFFF8F6),
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Configurar Limites",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E342E)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Campo Crítico (Quente demais)
                OutlinedTextField(
                    value = txtCritico,
                    onValueChange = { txtCritico = it },
                    label = { Text("Crítico (Muito Quente) ex: -5") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Alerta (Atenção)
                OutlinedTextField(
                    value = txtAlerta,
                    onValueChange = { txtAlerta = it },
                    label = { Text("Alerta (Atenção) ex: -10") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Muito Frio (Congelando demais)
                OutlinedTextField(
                    value = txtMuitoFrio,
                    onValueChange = { txtMuitoFrio = it },
                    label = { Text("Muito Frio (Excesso) ex: -30") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = Color(0xFF790001))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val dAlerta = txtAlerta.replace(",", ".").toDoubleOrNull() ?: atualAlerta
                            val dCritico = txtCritico.replace(",", ".").toDoubleOrNull() ?: atualCritico
                            val dMuitoFrio = txtMuitoFrio.replace(",", ".").toDoubleOrNull() ?: atualMuitoFrio

                            onSalvar(dAlerta, dCritico, dMuitoFrio)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF790001))
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}