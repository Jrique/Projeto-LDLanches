package com.ldlanches.demo.DTO

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length

data class UserDTO(
    @field:Email(message = "Você precisa informar um email valido")
    @field:NotEmpty(message = "O email precisa ser preenchido")
    val username : String,

    @Length(min = 5, max = 20, message="Sua senha precisa ter entre 5 e 20 caracteres")
    val password : String,

    val name :String = "",

    val perfil:String? = null
) {
    constructor():this(username="", password="", name="",perfil="")
}