package com.ldlanches.demo.Model

import com.ldlanches.demo.Enums.OrderStatus
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val code: String? = null,
    val description: String = "",
    val price: Double,
    val date: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    val status: OrderStatus = OrderStatus.NOVO
)
