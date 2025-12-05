package com.ldlanches.demo.DTO


data class LoginResponse(
    val token: String,
    val username: String,
    val name: String,
    val id: Long
)
