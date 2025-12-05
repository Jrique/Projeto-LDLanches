package com.ldlanches.demo.Service

import com.ldlanches.demo.DAO.CategoryDAO
import com.ldlanches.demo.Model.Category
import com.ldlanches.demo.Repository.CategoryRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun createCategory(categoryDAO: CategoryDAO): Category {
        val category = Category(
            categoryName = categoryDAO.categoryName
        )
        return categoryRepository.save(category)
    }

    fun getAllCategorys(): List<Category> = categoryRepository.findAll()

    fun getCategoryById(id: Long): Optional<Category> = categoryRepository.findById(id)

    fun updateCategory(id: Long, categoryDAO: CategoryDAO): Category? {
        return if (categoryRepository.existsById(id)) {
            val category = Category(
                id = id,
                categoryName = categoryDAO.categoryName
            )
            categoryRepository.save(category)
        } else {
            null
        }
    }

    fun deleteCategory(id: Long) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id)
        }
    }
}