package org.example.project.telas.componentes.tela.telaConfiguracoes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.example.project.getPlatform
import org.example.project.api.ApiProvider
import org.example.project.api.AuthDTO
import org.example.project.api.UserResponse
import org.example.project.data.Session
import org.example.project.data.UsuarioSessao

// Data class para simular um Admin
data class Admin(val id: Int, var nome: String, var email: String)

// Lista de admins de exemplo
private val adminsDeExemplo = listOf(
    Admin(1, "Henrique", "henrique@email.com"),
    Admin(2, "Luan Novais", "luan@email.com")
)

@Composable
fun ModalAlterarSenha(onDismiss: () -> Unit) { // Pode renomear para ModalEditarPerfil no uso
    val api = ApiProvider.lanchesApi
    val scope = rememberCoroutineScope()
    val usuarioAtual = Session.usuarioLogado

    var nome by remember { mutableStateOf(usuarioAtual?.nome ?: "") }
    var email by remember { mutableStateOf(usuarioAtual?.email ?: "") }
    var novaSenha by remember { mutableStateOf("") }

    var carregando by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFFF8F6),
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Editar Meu Perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email (Login)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = novaSenha,
                        onValueChange = { novaSenha = it },
                        label = { Text("Nova Senha (Deixe vazio para manter)") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF790001)) }
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    carregando = true
                                    val id = usuarioAtual?.id ?: return@launch

                                    val dto = AuthDTO(
                                        username = email,
                                        password = novaSenha, // Se vazio, backend ignora
                                        name = nome
                                    )
                                    api.atualizarUsuario(id, dto)

                                    // Atualiza a sessão local para refletir na UI imediatamente
                                    Session.usuarioLogado = UsuarioSessao(id, nome, email, usuarioAtual.token)

                                    onDismiss()
                                } catch (e: Exception) {
                                    println("Erro ao atualizar: $e")
                                } finally {
                                    carregando = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF790001)),
                        enabled = !carregando
                    ) {
                        Text(if (carregando) "Salvando..." else "Salvar Alterações")
                    }
                }
            }
        }
    }
}

@Composable
fun ModalGerarRelatorio(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gerar Relatório") },
        text = { Text("Esta funcionalidade ainda está em desenvolvimento.") },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF790001))) {
                Text("OK")
            }
        }
    )
}

@Composable
fun ModalGerenciarAdmins(onDismiss: () -> Unit) {
    val api = ApiProvider.lanchesApi
    val scope = rememberCoroutineScope()

    var admins by remember { mutableStateOf(emptyList<UserResponse>()) }
    var carregando by remember { mutableStateOf(true) }

    // Controle do Modal de Adicionar Novo Admin
    var mostrarFormularioNovo by remember { mutableStateOf(false) }

    fun carregarLista() {
        scope.launch {
            try {
                carregando = true
                admins = api.listarUsuarios()
            } catch (e: Exception) {
                println("Erro lista: $e")
            } finally {
                carregando = false
            }
        }
    }

    LaunchedEffect(Unit) { carregarLista() }

    if (mostrarFormularioNovo) {
        AdminFormModal(
            onDismiss = { mostrarFormularioNovo = false },
            onSave = { dto ->
                scope.launch {
                    try {
                        api.cadastrar(dto)
                        mostrarFormularioNovo = false
                        carregarLista() // Recarrega a lista após criar
                    } catch (e: Exception) {
                        println("Erro ao criar admin: $e")
                    }
                }
            }
        )
    } else {
        // Lista de Admins
        Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.8f),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF8F6)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Gerenciar Usuários", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { mostrarFormularioNovo = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF790001))
                        ) {
                            Text("Novo Admin")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (carregando) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF790001))
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(admins) { admin ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(admin.name, fontWeight = FontWeight.Bold)
                                        Text(admin.email, style = MaterialTheme.typography.bodySmall)
                                    }

                                    // Não permite excluir o próprio usuário logado para evitar crash
                                    if (admin.id != Session.usuarioLogado?.id) {
                                        IconButton(onClick = {
                                            scope.launch {
                                                api.deletarUsuario(admin.id)
                                                carregarLista()
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, "Excluir", tint = Color(0xFF790001))
                                        }
                                    } else {
                                        Text("(Você)", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                                HorizontalDivider(color = Color.LightGray.copy(0.3f))
                            }
                        }
                    }

                    Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                        Text("Fechar")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminFormModal(onDismiss: () -> Unit, onSave: (AuthDTO) -> Unit) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Administrador") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome Completo") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, visualTransformation = PasswordVisualTransformation())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if(nome.isNotBlank() && email.isNotBlank() && senha.isNotBlank()) {
                        onSave(AuthDTO(username = email, password = senha, name = nome))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF790001))
            ) { Text("Criar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF790001)) }
        }
    )
}

@Composable
private fun AdminFormModal(admin: Admin?, onDismiss: () -> Unit, onSave: () -> Unit) {
    var nome by remember { mutableStateOf(admin?.nome ?: "") }
    var email by remember { mutableStateOf(admin?.email ?: "") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (admin == null) "Adicionar Admin" else "Editar Admin") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome Completo") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text(if (admin == null) "Senha" else "Nova Senha (opcional)") }, visualTransformation = PasswordVisualTransformation())
                if (admin == null) {
                    OutlinedTextField(value = confirmarSenha, onValueChange = { confirmarSenha = it }, label = { Text("Confirmar Senha") }, visualTransformation = PasswordVisualTransformation())
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF790001))) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF790001)) }
        }
    )
}