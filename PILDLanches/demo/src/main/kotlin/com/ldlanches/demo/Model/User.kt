package com.ldlanches.demo.Model
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @field:Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? =null,

    @Column(length=100, unique = true, nullable = false)
    val email: String,

    @Column(length=255)
    val password: String,

    @Column(length=50)
    val name: String,

    @Column(length=50)
    val perfil: String?
){
    constructor() : this(null, email = "", password = "", name ="", perfil = "")
}