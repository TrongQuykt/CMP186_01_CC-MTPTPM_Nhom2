package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Brand;
import com.example.ecommerce.gearvn.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Brand findByName(String name);
    long count();
}

