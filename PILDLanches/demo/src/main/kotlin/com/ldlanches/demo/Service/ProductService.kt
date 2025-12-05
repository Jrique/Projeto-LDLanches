package com.ldlanches.demo.Service

import com.ldlanches.demo.DAO.ProductDAO
import com.ldlanches.demo.Model.Product
import com.ldlanches.demo.Repository.ProductRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun createProduct(productDAO: ProductDAO): Product {
        val product = Product(
            name = productDAO.name,
            price = productDAO.price,
            description = productDAO.description,
            type =productDAO.type,
            extra = productDAO.extra,
            highlighted = productDAO.highlighted,
            dateAdded = productDAO.dateAdded
        )
        return productRepository.save(product)
    }

    fun getAllProducts(): List<Product> = productRepository.findAll()

    fun getProductById(id: Long): Optional<Product> = productRepository.findById(id)

    fun updateProduct(id: Long, productDAO: ProductDAO): Product? {
        return if (productRepository.existsById(id)) {
            val product = Product(
                id = id,
                name = productDAO.name,
                price = productDAO.price,
                description = productDAO.description,
                type =productDAO.type,
                extra = productDAO.extra,
                highlighted = productDAO.highlighted,
                dateAdded = productDAO.dateAdded
            )
            productRepository.save(product)
        } else {
            null
        }
    }

    fun deleteProduct(id: Long) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
        }
    }

    fun setHighlight(id: Long, highlighted: Boolean): Product? {
        val productOpt = productRepository.findById(id)
        if (productOpt.isPresent) {
            val product = productOpt.get()
            val updated = product.copy(highlighted = highlighted)
            return productRepository.save(updated)
        }
        return null
    }
}