package com.ldlanches.demo.Repository

import com.ldlanches.demo.Model.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order,Long>