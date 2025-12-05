package org.example.project.telas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.project.api.ApiProvider

class MonitoramentoViewModel : ViewModel() {
    private val api = ApiProvider.lanchesApi

    var temperatura by mutableStateOf(0.0)
    var horario by mutableStateOf("--:--")
    var erro by mutableStateOf<String?>(null)

    // NOVOS ESTADOS
    var limiteAlerta by mutableStateOf(-10.0)
    var limiteCritico by mutableStateOf(-5.0)
    var limiteMuitoFrio by mutableStateOf(-30.0)

    init {
        iniciarMonitoramento()
    }

    fun atualizarLimites(alerta: Double, critico: Double, muitoFrio: Double) {
        limiteAlerta = alerta
        limiteCritico = critico
        limiteMuitoFrio = muitoFrio
    }

    private fun iniciarMonitoramento() {
        viewModelScope.launch {
            while (isActive) {
                try {
                    val dados = api.getTemperaturaFreezer()
                    temperatura = dados.temp
                    horario = dados.time
                    erro = null
                } catch (e: Exception) {
                    erro = "Desconectado"
                }
                delay(3000)
            }
        }
    }
}