package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.model.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    ProductInventory findByProduct(Product product);
}
