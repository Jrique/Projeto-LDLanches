package com.ldlanches.demo.DAO

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDate


data class ProductDAO (
    val id:Long,
    @Schema(description = "Nome do produto.", example = "X-Tudo", required = true)
    @field:NotBlank(message = "O produto deve ter nome")
    val name:String,
    @Schema(description = "Preço do produto em Reais (BRL).", example = "25.90")
    @field:Positive(message = "O preço do produto deve ser maior que zero")
    val price:Double,
    @Schema(description = "Ingredientes do produto" , example = "Hambúrguer completo com tudo que tem direito")
    val description:String,
    @Schema(description = "Tipo do produto", example = "Lanche, Bebida")
    val type:String,
    @Schema(description = "Acompanhamentos do produto", example = "Bacon, Ovo, Presunto")
    val extra:String,
    @Schema(description = "Data que ele foi adicionado", example = "2025-10-17")
    val dateAdded: LocalDate = LocalDate.now(),
    val highlighted: Boolean = false
)