package com.ldlanches.demo.DAO

import io.swagger.v3.oas.annotations.media.Schema


data class UserDAO (
    val id : Long?=null,
    @Schema(description = "Email do usuário", example = "example@gmail.com")
    val email : String,
    @Schema(description = "Senha do usuário", example = "12345")
    val password : String,
    @Schema(description = "Nome do usuário", example = "Exemplo da Silva")
    val name : String,
    @Schema(description = "Perfil de permissões do usuário", example = "ADMIN")
    val perfil:String
)