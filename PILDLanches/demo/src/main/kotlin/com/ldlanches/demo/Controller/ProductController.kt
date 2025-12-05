package com.ldlanches.demo.Controller

import com.ldlanches.demo.DAO.ProductDAO
import com.ldlanches.demo.Service.ProductService
import com.ldlanches.demo.DTO.ProductInDTO
import com.ldlanches.demo.DTO.ProductOutDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/products")
class ProductController(
    private val service: ProductService
) {
    @Operation(summary = "Lista todos os produtos de um sistema", description = "Retorna uma lista com todos os produtos cadastrados no sistema")
    @GetMapping
    fun all(): List<ProductOutDTO> =
        service.getAllProducts().map { p ->
            ProductOutDTO(
                id = p.id,
                name = p.name,
                description = p.description,
                price = p.price,
                type = p.type,
                extra = p.extra,
                highlighted = p.highlighted
            )
        }
    @Operation(summary = "Busca um produto pelo ID", description = "Retorna os detalhes de um produto específico.")
    @GetMapping("/{id}")
    fun one(@PathVariable id: Long): ResponseEntity<ProductOutDTO> =
        service.getProductById(id)
            .map { p ->
                ProductOutDTO(
                    id = p.id,
                    name = p.name,
                    description = p.description,
                    price = p.price,
                    type = p.type,
                    extra = p.extra,
                    highlighted = p.highlighted
                )
            }
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    @Operation(summary = "Cria um novo produto", description = "Registra um novo produto no sistema.")
    @PostMapping
    fun create(@RequestBody body: ProductInDTO): ProductOutDTO {
        // Completa os campos extras do seu domínio (type/extra/dateAdded)
        val dao = ProductDAO(
            id = 0,
            name = body.name,
            price = body.price,
            description = body.description ?: "",
            type = body.type ?: "",
            extra = body.extra ?: "",
            highlighted = body.highlighted,
            dateAdded = LocalDate.now()
        )
        val saved = service.createProduct(dao)
        return ProductOutDTO(
            id = saved.id,
            name = saved.name,
            description = saved.description,
            price = saved.price,
            type = saved.type,
            extra = saved.extra,
            highlighted = body.highlighted
        )
    }
    @Operation(summary = "Atualiza os dados de um produto", description = "Atualiza um produto no sistema")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody body: ProductInDTO): ResponseEntity<ProductOutDTO> {
        val dao = ProductDAO(
            id = id,
            name = body.name,
            price = body.price,
            description = body.description ?: "",
            type = body.type ?: "",
            extra = body.extra ?: "",
            highlighted = body.highlighted,
            dateAdded = LocalDate.now()
        )
        val updated = service.updateProduct(id, dao) ?: return ResponseEntity.notFound().build()
        val out = ProductOutDTO(
            id = updated.id,
            name = updated.name,
            description = updated.description,
            price = updated.price,
            type = updated.type,
            highlighted = body.highlighted,
            extra = updated.extra
        )
        return ResponseEntity.ok(out)
    }
    @Operation(summary = "Exclui um produto", description = "Remove um produto do sistema pelo seu ID.")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/highlight")
    fun toggleHighlight(
        @PathVariable id: Long,
        @RequestParam highlighted: Boolean
    ): ResponseEntity<ProductOutDTO> {
        val updated = service.setHighlight(id, highlighted) ?: return ResponseEntity.notFound().build()

        val out = ProductOutDTO(
            id = updated.id,
            name = updated.name,
            description = updated.description,
            price = updated.price,
            type = updated.type,
            extra = updated.extra,
            highlighted = updated.highlighted
        )
        return ResponseEntity.ok(out)
    }
}
