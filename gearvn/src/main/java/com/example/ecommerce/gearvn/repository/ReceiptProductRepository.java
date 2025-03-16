package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.ReceiptProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptProductRepository extends JpaRepository<ReceiptProduct, Long> {
}
