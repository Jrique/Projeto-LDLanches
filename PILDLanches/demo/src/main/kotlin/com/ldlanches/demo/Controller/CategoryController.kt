package com.ldlanches.demo.Controller

import com.ldlanches.demo.DAO.CategoryDAO
import com.ldlanches.demo.Model.Category
import com.ldlanches.demo.Service.CategoryService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/category")
class CategoryController(private val productService: CategoryService) {
    @Operation(summary = "Cria um novo categoria", description = "Registra um novo categoria no sistema.")
    @PostMapping
    fun createCategory(@Valid @RequestBody productDAO: CategoryDAO): ResponseEntity<Category> {
        val createdCategory = productService.createCategory(productDAO)
        return ResponseEntity(createdCategory, HttpStatus.CREATED)
    }
    @Operation(summary = "Lista todos os categorias", description = "Retorna uma lista com todos os categorias cadastrados.")
    @GetMapping
    fun getAllCategorys(): ResponseEntity<List<Category>> {
        val products = productService.getAllCategorys()
        return ResponseEntity(products, HttpStatus.OK)
    }
    @Operation(summary = "Busca um categoria pelo ID", description = "Retorna os detalhes de um categoria específico.")
    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Long): ResponseEntity<Category> {
        return productService.getCategoryById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }
    @Operation(summary = "Atualiza um categoria existente", description = "Altera os dados de um categoria com base no seu ID.")
    @PutMapping("/{id}")
    fun updateCategory(@PathVariable id: Long, @Valid @RequestBody productDAO: CategoryDAO): ResponseEntity<Category> {
        val updatedCategory = productService.updateCategory(id, productDAO)
        return updatedCategory
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
    @Operation(summary = "Exclui um categoria", description = "Remove um categoria do sistema pelo seu ID.")
    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteCategory(id)
        return ResponseEntity(HttpStatus.OK)
    }
}