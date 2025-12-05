package com.ldlanches.demo.DAO

import io.swagger.v3.oas.annotations.media.Schema


data class CategoryDAO (
    val id:Long = 0,
    @Schema(description = "Nome da categoria do produto", example = "Lanches")
    val categoryName:String
)