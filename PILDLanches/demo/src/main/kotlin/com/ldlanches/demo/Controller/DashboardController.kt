package com.ldlanches.demo.Controller

import com.ldlanches.demo.Service.OrderService
import com.ldlanches.demo.DTO.DashboardDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@RestController
@RequestMapping("/dashboard")
class DashboardController(val orderService: OrderService) {

    @GetMapping("/resumo")
    fun getResumo(
        @RequestParam(defaultValue = "7") dias: Int,
        @RequestParam(required = false) inicio: String?,
        @RequestParam(required = false) fim: String?
    ): ResponseEntity<DashboardDTO> {
        val pedidos = orderService.getAllOrders()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val dataFinal: LocalDate
        val dataInicial: LocalDate

        if (inicio != null && fim != null) {
            dataInicial = LocalDate.parse(inicio, formatter)
            dataFinal = LocalDate.parse(fim, formatter)
        } else {
            dataFinal = LocalDate.now()
            dataInicial = dataFinal.minusDays((dias - 1).toLong())
        }

        val pedidosNoPeriodo = pedidos.filter {
            val d = it.date.toLocalDate()
            (d.isEqual(dataInicial) || d.isAfter(dataInicial)) &&
                    (d.isEqual(dataFinal) || d.isBefore(dataFinal))
        }

        val faturamento = mutableListOf<Double>()
        val vendas = mutableListOf<Int>()
        val diasLabel = mutableListOf<String>()

        val qtdDias = ChronoUnit.DAYS.between(dataInicial, dataFinal).toInt()

        for (i in 0..qtdDias) {
            val dataAtual = dataInicial.plusDays(i.toLong())

            val pedidosDoDia = pedidosNoPeriodo.filter { it.date.toLocalDate().isEqual(dataAtual) }

            faturamento.add(pedidosDoDia.sumOf { it.price })
            vendas.add(pedidosDoDia.size)

            val diaSemana = dataAtual.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
            val label = "$diaSemana ${dataAtual.dayOfMonth}/${dataAtual.monthValue}"
            diasLabel.add(label.replace(".", "").replaceFirstChar { it.uppercase() })
        }

        return ResponseEntity.ok(DashboardDTO(
            faturamentoDiario = faturamento,
            vendasDiarias = vendas,
            diasDaSemana = diasLabel,
            totalFaturamento = pedidosNoPeriodo.sumOf { it.price },
            totalVendas = pedidosNoPeriodo.size
        ))
    }
}