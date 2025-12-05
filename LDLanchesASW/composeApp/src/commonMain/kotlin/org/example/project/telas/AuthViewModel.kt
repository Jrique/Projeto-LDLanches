package org.example.project.telas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.api.ApiProvider
import org.example.project.api.AuthDTO
import org.example.project.api.LanchesApi
import org.example.project.data.Session
import org.example.project.data.UsuarioSessao

class AuthViewModel : ViewModel() {
    private val api = ApiProvider.lanchesApi

    var login by mutableStateOf("")
    var senha by mutableStateOf("")
    var nome by mutableStateOf("")

    var carregando by mutableStateOf(false)
    var erro by mutableStateOf<String?>(null)

    fun realizarLogin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                carregando = true
                erro = null
                val dto = AuthDTO(username = login, password = senha)

                val resp = api.login(dto)

                Session.usuarioLogado = UsuarioSessao(
                    id = resp.id,
                    nome = resp.name,
                    email = resp.username,
                    token = resp.token
                )
                onSuccess()
            } catch (e: Exception) {
                erro = "Erro no login: ${e.message}"
                e.printStackTrace()
            } finally {
                carregando = false
            }
        }
    }

    fun realizarCadastro(onSuccess: () -> Unit) {
        if (nome.isBlank()) {
            erro = "Digite seu nome para cadastro"
            return
        }
        viewModelScope.launch {
            try {
                carregando = true
                erro = null
                val dto = AuthDTO(username = login, password = senha, name = nome)

                api.cadastrar(dto)

                val resp = api.login(dto)

                Session.usuarioLogado = UsuarioSessao(
                    id = resp.id,
                    nome = resp.name,
                    email = resp.username,
                    token = resp.token
                )

                onSuccess()
            } catch (e: Exception) {
                erro = "Erro ao criar conta: ${e.message}"
            } finally {
                carregando = false
            }
        }
    }
}