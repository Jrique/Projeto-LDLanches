package com.ldlanches.demo.Model

import jakarta.persistence.*

@Entity
@Table(name = "category")
data class Category (
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Long = 0,
    val categoryName:String
)