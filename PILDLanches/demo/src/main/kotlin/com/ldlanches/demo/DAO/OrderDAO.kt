package com.ldlanches.demo.DAO


import com.ldlanches.demo.Enums.OrderStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDate


data class OrderDAO (
    val id: Long = 0,
    @Schema(description = "Descrição do pedido", example = "X-tudo")
    @field:NotBlank(message = "O pedido não deve estar vazio")
    val description: String,
    @field:Positive(message = "O preço do pedido deve ser maior que zero")
    @Schema(description = "Preço total do pedido", example = "29.99")
    val price: Double,
    val code: String,
    @Schema(description = "Data que ele foi criado", example = "2025-10-17")
    val date: LocalDate = LocalDate.now(),
    @Schema(description = "Status do pedido", example = "NOVO")
    val status: OrderStatus = OrderStatus.NOVO
)