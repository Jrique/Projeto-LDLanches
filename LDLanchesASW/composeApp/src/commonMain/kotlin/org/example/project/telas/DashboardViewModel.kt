package org.example.project.telas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.api.ApiProvider
import org.example.project.api.DashboardDTO
import org.example.project.telas.componentes.tela.telaFaturamento.PeriodoFiltroGlobal

class DashboardViewModel : ViewModel() {
    private val api = ApiProvider.lanchesApi

    var dados by mutableStateOf<DashboardDTO?>(null)
    var carregando by mutableStateOf(false)

    // Estado do filtro selecionado
    var filtroAtual by mutableStateOf(PeriodoFiltroGlobal.SEMANAL)

    init {
        carregar(PeriodoFiltroGlobal.SEMANAL)
    }

    fun carregar(periodo: PeriodoFiltroGlobal) {
        filtroAtual = periodo
        val dias = when(periodo) {
            PeriodoFiltroGlobal.HOJE -> 1
            PeriodoFiltroGlobal.SEMANAL -> 7
            PeriodoFiltroGlobal.MENSAL -> 30
        }

        viewModelScope.launch {
            try {
                carregando = true
                dados = api.getDashboardResumo(dias)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                carregando = false
            }
        }
    }

    fun carregarPersonalizado(inicio: String, fim: String) {
        viewModelScope.launch {
            try {
                carregando = true
                // Envia dias=0 (backend ignora se tiver datas) e as datas
                dados = api.getDashboardResumo(dias = 0, inicio = inicio, fim = fim)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                carregando = false
            }
        }
    }
}