package com.ldlanches.demo.Service

import com.ldlanches.demo.DAO.OrderDAO
import com.ldlanches.demo.Enums.OrderStatus
import com.ldlanches.demo.Model.Order
import com.ldlanches.demo.Repository.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val repository: OrderRepository
) {

    fun createOrder(orderDAO: OrderDAO): Order {
        val generatedCode = orderDAO.code.ifBlank { gerarCodigoUnico() }
        val order = Order(
            code = generatedCode,
            description = orderDAO.description,
            price = orderDAO.price,
            status = OrderStatus.NOVO
        )
        return repository.save(order)
    }

    fun getAllOrders(): List<Order> =
        repository.findAll()

    fun getOrderById(id: Long): Order? =
        repository.findById(id).orElse(null)

    fun updateOrder(id: Long, orderDAO: OrderDAO): Order? {
        val existing = repository.findById(id).orElse(null) ?: return null

        val updated = existing.copy(
            code = orderDAO.code,              // se quiser, pode optar por não deixar code editável
            description = orderDAO.description,
            price = orderDAO.price
        )

        return repository.save(updated)
    }

    fun atualizarStatus(id: Long, novoStatus: OrderStatus): Order? {
        val existing = repository.findById(id).orElse(null) ?: return null
        val updated = existing.copy(status = novoStatus)
        return repository.save(updated)
    }

    fun deleteOrder(id: Long) {
        repository.deleteById(id)
    }
}

private fun gerarCodigoUnico(): String {
    val stamp = System.currentTimeMillis().toString(36).uppercase()
    return "LDL-${stamp.takeLast(6)}"
}