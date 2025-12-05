package com.ldlanches.demo.DTO

data class DashboardDTO(
    val faturamentoDiario: List<Double>,
    val vendasDiarias: List<Int>,
    val diasDaSemana: List<String>,
    val totalFaturamento: Double,
    val totalVendas: Int
)