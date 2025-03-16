package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
    long count();
}