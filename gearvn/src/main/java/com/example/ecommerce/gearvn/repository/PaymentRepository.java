package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Order;
import com.example.ecommerce.gearvn.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
    List<Payment> findByStatus(int status);
}
