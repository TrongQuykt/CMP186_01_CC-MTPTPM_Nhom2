package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Order;
import com.example.ecommerce.gearvn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByStatus(int status);

    List<Order> findByStatusNot(int status);

    List<Order> findByStatusNotIn(List<Integer> statuses);

    long countByStatus(int status);

    // Replace createDay with orderDate
    List<Order> findByOrderDateBetweenAndStatusNot(LocalDateTime startDate, LocalDateTime endDate, int status);
    List<Order> findByOrderDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, int status);
    int countByStatusIn(List<Integer> statuses);
    
}
