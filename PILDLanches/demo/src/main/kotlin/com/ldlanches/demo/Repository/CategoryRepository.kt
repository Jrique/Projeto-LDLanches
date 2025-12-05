package com.ldlanches.demo.Repository

import com.ldlanches.demo.Model.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category,Long>