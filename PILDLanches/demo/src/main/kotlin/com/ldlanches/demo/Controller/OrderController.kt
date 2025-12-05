package com.ldlanches.demo.controller

import com.ldlanches.demo.DAO.OrderDAO
import com.ldlanches.demo.Enums.OrderStatus
import com.ldlanches.demo.Model.Order
import com.ldlanches.demo.Service.OrderService
import com.ldlanches.demo.DTO.OrderInDTO
import com.ldlanches.demo.DTO.OrderOutDTO
import com.ldlanches.demo.DTO.OrderStatusUpdateDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import org.springframework.web.bind.annotation.CrossOrigin
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/order")
class OrderController(
    private val service: OrderService
) {
    @Operation(summary = "Cria um novo pedido", description = "Registra um novo pedido no sistema.")
    @PostMapping
    fun create(@RequestBody dto: OrderInDTO): ResponseEntity<OrderOutDTO> {
        val dao = OrderDAO(
            code = dto.code,
            description = dto.description ?: "",
            price = dto.total
        )

        val saved = service.createOrder(dao)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(saved.toOutDTO())
    }

    @Operation(summary = "Lista todos os pedidos", description = "Retorna uma lista com todos os pedidos cadastrados.")
    @GetMapping
    fun findAll(): List<OrderOutDTO> =
        service.getAllOrders().map { it.toOutDTO() }
    @Operation(summary = "Busca um pedido pelo ID", description = "Retorna os detalhes de um pedido específico.")
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<OrderOutDTO> {
        val order = service.getOrderById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(order.toOutDTO())
    }
    @Operation(summary = "Atualiza um pedido existente", description = "Altera os dados de um pedido com base no seu ID.")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody dto: OrderInDTO
    ): ResponseEntity<OrderOutDTO> {
        val dao = OrderDAO(
            id = id,
            code = dto.code,
            description = dto.description ?: "",
            price = dto.total
        )

        val updated = service.updateOrder(id, dao) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updated.toOutDTO())
    }
    @Operation(summary = "Atualiza o status de um pedido existente", description = "Altera o status de um pedido com base no seu ID.")
    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody dto: OrderStatusUpdateDTO
    ): ResponseEntity<OrderOutDTO> {
        val statusEnum = try {
            OrderStatus.valueOf(dto.status)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }

        val updated = service.atualizarStatus(id, statusEnum) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updated.toOutDTO())
    }
    @Operation(summary = "Exclui um pedido", description = "Remove um pedido do sistema pelo seu ID.")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.deleteOrder(id)
        return ResponseEntity.noContent().build()
    }

    // Mapper entidade -> DTO
    private fun Order.toOutDTO() =
        OrderOutDTO(
            id = id,
            code = code ?: "",
            total = price,
            description = description,
            date = formatDateLabel(date),
            status = status.name
        )

    private fun formatDateLabel(dateTime: LocalDateTime): String {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val datePart = dateTime.toLocalDate()
        val timePart = dateTime.toLocalTime()
            .withSecond(0)
            .withNano(0)

        val prefix = when {
            datePart.isEqual(today)      -> "Hoje"
            datePart.isEqual(yesterday)  -> "Ontem"
            else -> datePart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }

        val hora = timePart.format(DateTimeFormatter.ofPattern("HH:mm"))

        return "$prefix, $hora"
    }

}
