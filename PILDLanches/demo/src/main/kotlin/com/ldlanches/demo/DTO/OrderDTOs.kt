package com.ldlanches.demo.DTO


data class OrderInDTO(
    val code: String,
    val total: Double,
    // Detalhes do pedido (produtos, endereço, retirada, forma de pagto etc.)
    val description: String? = null
)

data class OrderOutDTO(
    val id: Long,
    val code: String,
    val total: Double,
    val description: String,
    val date: String,
    val status: String
)

data class OrderStatusUpdateDTO(
    val status: String
)