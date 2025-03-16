package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Cart;
import com.example.ecommerce.gearvn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);
    Cart findByUser(User user);

}
