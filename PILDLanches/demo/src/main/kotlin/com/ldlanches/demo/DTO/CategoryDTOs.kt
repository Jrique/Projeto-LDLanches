package com.ldlanches.demo.DTO


import com.ldlanches.demo.Model.Category
import jakarta.validation.constraints.NotBlank

data class CategoryInDTO(
    @field:NotBlank
    val categoryName: String
)

data class CategoryOutDTO(
    val id: Long,
    val categoryName: String
)

fun Category.toOutDTO() = CategoryOutDTO(
    id = this.id,
    categoryName = this.categoryName
)
