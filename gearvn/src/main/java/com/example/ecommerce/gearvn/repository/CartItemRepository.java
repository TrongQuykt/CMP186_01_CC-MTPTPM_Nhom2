package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Cart;
import com.example.ecommerce.gearvn.model.CartItem;
import com.example.ecommerce.gearvn.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    CartItem findByCartAndProduct(Cart cart, Product product);
    void delete(CartItem cartItem);
}
