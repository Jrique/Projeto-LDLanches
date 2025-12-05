package com.ldlanches.demo.DTO

data class ProductInDTO(
    val name: String,
    val description: String? = null,
    val price: Double,
    val type: String? = null,
    val extra: String? = null,
    val highlighted: Boolean = false
)

data class ProductOutDTO(
    val id: Long,
    val name: String,
    val description: String? = null,
    val price: Double,
    val type: String? = null,
    val extra: String? = null,
    val highlighted: Boolean = false
)
