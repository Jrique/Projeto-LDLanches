package org.example.project.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ldlanchesasw.composeapp.generated.resources.Res
import ldlanchesasw.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun TelaLogin(onLoginSuccess: () -> Unit) {
    val vm = viewModel { AuthViewModel() }
    var isCadastro by remember { mutableStateOf(false) } // Controla o modo da tela

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(vm.erro) {
        vm.erro?.let { snackbarHostState.showSnackbar(it) }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFF8F6)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.width(400.dp).padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE9E6))
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(painter = painterResource(Res.drawable.logo), contentDescription = null, modifier = Modifier.size(100.dp))

                Text(
                    if (isCadastro) "Criar Nova Conta" else "Login",
                    style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold
                )

                // Campo NOME só no cadastro
                if (isCadastro) {
                    OutlinedTextField(
                        value = vm.nome,
                        onValueChange = { vm.nome = it },
                        label = { Text("Seu Nome (para exibição)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = vm.login,
                    onValueChange = { vm.login = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = vm.senha,
                    onValueChange = { vm.senha = it },
                    label = { Text("Senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (vm.carregando) CircularProgressIndicator(color = Color(0xFF790001))

                Button(
                    onClick = {
                        if (isCadastro) vm.realizarCadastro(onLoginSuccess)
                        else vm.realizarLogin(onLoginSuccess)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF790001)),
                    enabled = !vm.carregando
                ) {
                    Text(if (isCadastro) "Cadastrar" else "Entrar")
                }

                TextButton(onClick = { isCadastro = !isCadastro }) {
                    Text(
                        if (isCadastro) "Já tem conta? Fazer Login" else "Não tem conta? Criar agora",
                        color = Color(0xFF790001)
                    )
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}