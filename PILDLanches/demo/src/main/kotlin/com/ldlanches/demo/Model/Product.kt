package com.ldlanches.demo.Model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
data class Product (
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    val id:Long = 0,
    val name:String,
    val price:Double,
    val description:String,
    val type:String,
    val extra:String,
    val dateAdded: LocalDate = LocalDate.now(),
    val highlighted: Boolean = false

)
